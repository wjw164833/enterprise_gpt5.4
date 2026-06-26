package com.invitation.web.controller;

import com.invitation.common.constant.RedisKeyConstant;
import com.invitation.common.model.R;
import com.invitation.common.util.IpUtil;
import com.invitation.common.util.UserAgentUtil;
import com.invitation.infra.redis.RedisService;
import com.invitation.model.dto.analytics.AnalyticsEventDTO;
import com.invitation.model.dto.invitation.InvitationDetailVO;
import com.invitation.service.analytics.AnalyticsService;
import com.invitation.service.invitation.InvitationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 邀请函访客端控制器（无需登录）
 */
@Tag(name = "邀请函访客端")
@Slf4j
@RestController
@RequestMapping("/api/v1/guest/i")
@RequiredArgsConstructor
public class InvitationViewController {

    private final InvitationService invitationService;
    private final AnalyticsService analyticsService;
    private final RedisService redisService;
    private final IpUtil ipUtil;
    private final UserAgentUtil userAgentUtil;

    @Operation(summary = "通过短码查看邀请函")
    @GetMapping("/{shortCode}")
    public R<InvitationDetailVO> viewByShortCode(@PathVariable String shortCode, HttpServletRequest request) {
        String ip = ipUtil.getClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        String platform = userAgentUtil.parsePlatform(userAgent);

        R<InvitationDetailVO> result = invitationService.getInvitationByShortCode(shortCode);
        if (result.getData() != null) {
            Long invitationId = result.getData().getId();

            String pvKey = RedisKeyConstant.INVITATION_PV_COUNT + invitationId;
            redisService.increment(pvKey);

            String uvKey = RedisKeyConstant.INVITATION_UV_COUNT + invitationId;
            redisService.hyperLogLogAdd(uvKey, ip);

            try {
                AnalyticsEventDTO eventDTO = new AnalyticsEventDTO();
                eventDTO.setInvitationId(invitationId);
                eventDTO.setEventType("view");
                eventDTO.setEventName("invitation_view");
                eventDTO.setIp(ip);
                eventDTO.setUserAgent(userAgent);
                eventDTO.setExtraData(platform);
                analyticsService.trackEvent(eventDTO);
            } catch (Exception e) {
                log.warn("记录浏览事件失败", e);
            }
        }
        return result;
    }

    @Operation(summary = "通过ID查看邀请函")
    @GetMapping("/detail/{id}")
    public R<InvitationDetailVO> viewById(@PathVariable Long id, HttpServletRequest request) {
        String ip = ipUtil.getClientIp(request);

        String pvKey = RedisKeyConstant.INVITATION_PV_COUNT + id;
        redisService.increment(pvKey);

        String uvKey = RedisKeyConstant.INVITATION_UV_COUNT + id;
        redisService.hyperLogLogAdd(uvKey, ip);

        return invitationService.getInvitationDetail(id);
    }
}
