package com.invitation.service.analytics;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.invitation.common.constant.RedisKeyConstant;
import com.invitation.infra.redis.RedisService;
import com.invitation.model.entity.Invitation;
import com.invitation.model.mapper.InvitationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 浏览量异步持久化处理器
 * 定时将Redis中的PV/UV数据刷入MySQL
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ViewCountHandler {

    private final RedisService redisService;
    private final InvitationMapper invitationMapper;

    /**
     * 每5分钟将Redis PV增量持久化到数据库
     */
    @Scheduled(fixedRate = 300000)
    public void persistViewCounts() {
        log.info("开始持久化浏览量数据...");

        Set<String> pvKeys = redisService.keys(RedisKeyConstant.INVITATION_PV_COUNT + "*");
        if (pvKeys == null || pvKeys.isEmpty()) {
            return;
        }

        int count = 0;
        for (String pvKey : pvKeys) {
            try {
                String invitationIdStr = pvKey.replace(RedisKeyConstant.INVITATION_PV_COUNT, "");
                Long invitationId = Long.parseLong(invitationIdStr);
                Integer pvIncrement = redisService.get(pvKey, Integer.class);

                if (pvIncrement != null && pvIncrement > 0) {
                    for (int i = 0; i < pvIncrement; i++) {
                        invitationMapper.incrementPv(invitationId);
                    }
                    redisService.delete(pvKey);
                    count++;
                }
            } catch (Exception e) {
                log.error("持久化PV数据失败: key={}", pvKey, e);
            }
        }

        log.info("浏览量持久化完成: 共处理{}条记录", count);
    }

    /**
     * 每10分钟将Redis UV增量持久化到数据库
     */
    @Scheduled(fixedRate = 600000)
    public void persistUvCounts() {
        log.info("开始持久化UV数据...");

        Set<String> uvKeys = redisService.keys(RedisKeyConstant.INVITATION_UV_COUNT + "*");
        if (uvKeys == null || uvKeys.isEmpty()) {
            return;
        }

        int count = 0;
        for (String uvKey : uvKeys) {
            try {
                String invitationIdStr = uvKey.replace(RedisKeyConstant.INVITATION_UV_COUNT, "");
                Long invitationId = Long.parseLong(invitationIdStr);
                Long uvCount = redisService.hyperLogLogCount(uvKey);

                if (uvCount != null && uvCount > 0) {
                    for (int i = 0; i < uvCount.intValue(); i++) {
                        invitationMapper.incrementUv(invitationId);
                    }
                    redisService.delete(uvKey);
                    count++;
                }
            } catch (Exception e) {
                log.error("持久化UV数据失败: key={}", uvKey, e);
            }
        }

        log.info("UV持久化完成: 共处理{}条记录", count);
    }
}
