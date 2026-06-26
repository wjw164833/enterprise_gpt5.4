package com.invitation.service.analytics;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.invitation.common.constant.RedisKeyConstant;
import com.invitation.common.model.LoginUser;
import com.invitation.common.model.R;
import com.invitation.common.util.IpUtil;
import com.invitation.infra.redis.RedisService;
import com.invitation.model.dto.analytics.AnalyticsEventDTO;
import com.invitation.model.dto.analytics.DashboardVO;
import com.invitation.model.entity.AnalyticsEvent;
import com.invitation.model.entity.GuestReply;
import com.invitation.model.entity.Invitation;
import com.invitation.model.entity.ViewLog;
import com.invitation.model.mapper.AnalyticsEventMapper;
import com.invitation.model.mapper.GuestReplyMapper;
import com.invitation.model.mapper.InvitationMapper;
import com.invitation.model.mapper.ViewLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 数据分析服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final AnalyticsEventMapper analyticsEventMapper;
    private final InvitationMapper invitationMapper;
    private final GuestReplyMapper guestReplyMapper;
    private final ViewLogMapper viewLogMapper;
    private final RedisService redisService;

    @Override
    @Async("taskExecutor")
    public R<Void> trackEvent(AnalyticsEventDTO dto) {
        AnalyticsEvent event = new AnalyticsEvent();
        event.setInvitationId(dto.getInvitationId());
        event.setEventType(dto.getEventType() != null ? dto.getEventType() : dto.getEventName());
        event.setEventName(dto.getEventName());
        event.setUserId(dto.getUserId());
        event.setIpAddress(dto.getIp());
        event.setUserAgent(dto.getUserAgent());
        event.setExtraData(dto.getExtraData());
        analyticsEventMapper.insert(event);

        String countKey = RedisKeyConstant.ANALYTICS_EVENT_COUNT + dto.getInvitationId() + ":" + dto.getEventName();
        redisService.increment(countKey);
        redisService.expire(countKey, 7, TimeUnit.DAYS);

        return R.ok();
    }

    @Override
    public R<DashboardVO> getDashboard(Long userId) {
        DashboardVO dashboard = new DashboardVO();

        LambdaQueryWrapper<Invitation> invWrapper = new LambdaQueryWrapper<>();
        invWrapper.eq(Invitation::getUserId, userId);
        Long totalInvitations = invitationMapper.selectCount(invWrapper);

        LambdaQueryWrapper<Invitation> pubWrapper = new LambdaQueryWrapper<>();
        pubWrapper.eq(Invitation::getUserId, userId)
                  .eq(Invitation::getStatus, 1);
        Long publishedInvitations = invitationMapper.selectCount(pubWrapper);

        // P1-06: 使用聚合查询替代N+1循环
        List<Invitation> myInvitations = invitationMapper.selectList(invWrapper);
        long totalViews = 0;
        long totalBlessings = 0;
        List<Long> invitationIds = new ArrayList<>();
        for (Invitation inv : myInvitations) {
            totalViews += inv.getPv() != null ? inv.getPv() : 0;
            totalBlessings += inv.getBlessCount() != null ? inv.getBlessCount() : 0;
            invitationIds.add(inv.getId());
        }

        long totalReplies = 0;
        long attendCount = 0;
        if (!invitationIds.isEmpty()) {
            // 批量查询总回复数
            LambdaQueryWrapper<GuestReply> replyWrapper = new LambdaQueryWrapper<>();
            replyWrapper.in(GuestReply::getInvitationId, invitationIds);
            totalReplies = guestReplyMapper.selectCount(replyWrapper);

            // 批量查询出席数
            LambdaQueryWrapper<GuestReply> attendWrapper = new LambdaQueryWrapper<>();
            attendWrapper.in(GuestReply::getInvitationId, invitationIds)
                         .eq(GuestReply::getReplyStatus, 1);
            attendCount = guestReplyMapper.selectCount(attendWrapper);
        }

        dashboard.setTotalInvitations(totalInvitations);
        dashboard.setPublishedInvitations(publishedInvitations);
        dashboard.setTotalViews(totalViews);
        dashboard.setTotalReplies(totalReplies);
        dashboard.setAttendCount(attendCount);
        dashboard.setTotalBlessings(totalBlessings);

        return R.ok(dashboard);
    }

    @Override
    public R<DashboardVO.InvitationStats> getInvitationStats(Long invitationId) {
        Invitation invitation = invitationMapper.selectById(invitationId);
        if (invitation == null) {
            return R.fail("邀请函不存在");
        }

        DashboardVO.InvitationStats stats = new DashboardVO.InvitationStats();
        stats.setInvitationId(invitationId);
        stats.setTitle(invitation.getTitle());
        stats.setPvCount(invitation.getPv() != null ? invitation.getPv() : 0);
        stats.setUvCount(invitation.getUv() != null ? invitation.getUv() : 0);
        stats.setReplyCount(invitation.getReplyCount() != null ? invitation.getReplyCount() : 0);
        stats.setAttendCount(invitation.getAttendCount() != null ? invitation.getAttendCount() : 0);
        stats.setBlessCount(invitation.getBlessCount() != null ? invitation.getBlessCount() : 0);

        LambdaQueryWrapper<GuestReply> attendWrapper = new LambdaQueryWrapper<>();
        attendWrapper.eq(GuestReply::getInvitationId, invitationId)
                     .eq(GuestReply::getReplyStatus, 1);
        stats.setAttendCount(guestReplyMapper.selectCount(attendWrapper).intValue());

        LambdaQueryWrapper<GuestReply> uncertainWrapper = new LambdaQueryWrapper<>();
        uncertainWrapper.eq(GuestReply::getInvitationId, invitationId)
                        .eq(GuestReply::getReplyStatus, 2);
        stats.setUncertainCount(guestReplyMapper.selectCount(uncertainWrapper));

        LambdaQueryWrapper<GuestReply> notAttendWrapper = new LambdaQueryWrapper<>();
        notAttendWrapper.eq(GuestReply::getInvitationId, invitationId)
                        .eq(GuestReply::getReplyStatus, 3);
        stats.setNotAttendCount(guestReplyMapper.selectCount(notAttendWrapper));

        return R.ok(stats);
    }

    @Override
    public R<DashboardVO.TrendData> getTrendData(Long invitationId, String period) {
        DashboardVO.TrendData trendData = new DashboardVO.TrendData();
        List<String> dates = new ArrayList<>();
        List<Long> viewCounts = new ArrayList<>();
        List<Long> replyCounts = new ArrayList<>();

        int days = "week".equals(period) ? 7 : ("month".equals(period) ? 30 : 7);
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);

        // P1-07: 查询view_log表按日期聚合PV/UV
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Map<String, Long> pvMap = new HashMap<>();
        Map<String, Long> replyMap = new HashMap<>();

        // 查询时间范围内的浏览日志
        LambdaQueryWrapper<ViewLog> viewWrapper = new LambdaQueryWrapper<>();
        viewWrapper.eq(ViewLog::getInvitationId, invitationId);
        Date startJavaDate = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endJavaDate = Date.from(endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        viewWrapper.ge(ViewLog::getViewDate, startJavaDate);
        viewWrapper.lt(ViewLog::getViewDate, endJavaDate);
        List<ViewLog> viewLogs = viewLogMapper.selectList(viewWrapper);

        for (ViewLog vl : viewLogs) {
            String dateStr = "";
            if (vl.getViewDate() != null) {
                dateStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format(vl.getViewDate());
            } else if (vl.getCreatedAt() != null) {
                dateStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format(vl.getCreatedAt());
            }
            if (!dateStr.isEmpty()) {
                pvMap.merge(dateStr, 1L, Long::sum);
            }
        }

        // 查询回复按日期统计
        LambdaQueryWrapper<GuestReply> replyWrapper = new LambdaQueryWrapper<>();
        replyWrapper.eq(GuestReply::getInvitationId, invitationId);
        List<GuestReply> replies = guestReplyMapper.selectList(replyWrapper);
        for (GuestReply gr : replies) {
            if (gr.getCreatedAt() != null) {
                String dateStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format(gr.getCreatedAt());
                replyMap.merge(dateStr, 1L, Long::sum);
            }
        }

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            String dateStr = date.format(formatter);
            dates.add(dateStr);
            viewCounts.add(pvMap.getOrDefault(dateStr, 0L));
            replyCounts.add(replyMap.getOrDefault(dateStr, 0L));
        }

        trendData.setDates(dates);
        trendData.setViewCounts(viewCounts);
        trendData.setReplyCounts(replyCounts);

        return R.ok(trendData);
    }

    @Override
    public R<DashboardVO.SourceDistribution> getSourceDistribution(Long invitationId) {
        DashboardVO.SourceDistribution distribution = new DashboardVO.SourceDistribution();

        // 查询view_log表按来源分组统计
        LambdaQueryWrapper<ViewLog> viewWrapper = new LambdaQueryWrapper<>();
        viewWrapper.eq(ViewLog::getInvitationId, invitationId);
        List<ViewLog> viewLogs = viewLogMapper.selectList(viewWrapper);

        Map<String, Long> sourceMap = new LinkedHashMap<>();
        sourceMap.put("微信小程序", 0L);
        sourceMap.put("微信H5", 0L);
        sourceMap.put("浏览器", 0L);
        sourceMap.put("其他", 0L);

        for (ViewLog vl : viewLogs) {
            String source = vl.getSource() != null ? vl.getSource() : "其他";
            // 根据source字段映射到中文分类
            String category = "其他";
            if (source.contains("miniapp") || source.contains("小程序")) {
                category = "微信小程序";
            } else if (source.contains("wxh5") || source.contains("微信H5")) {
                category = "微信H5";
            } else if (source.contains("browser") || source.contains("浏览器")) {
                category = "浏览器";
            }
            sourceMap.merge(category, 1L, Long::sum);
        }

        distribution.setSources(sourceMap);

        return R.ok(distribution);
    }
}
