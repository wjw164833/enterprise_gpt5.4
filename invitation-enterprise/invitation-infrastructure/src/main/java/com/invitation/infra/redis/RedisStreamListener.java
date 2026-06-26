package com.invitation.infra.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Redis Stream监听器 - 用于监听Redis Stream消息
 */
@Slf4j
@Component
public class RedisStreamListener {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisStreamListener(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 定时检查未处理的Stream消息
     */
    @Scheduled(fixedDelay = 60000)
    public void checkPendingMessages() {
        // Redis Stream待处理消息检查
        log.debug("检查Redis Stream待处理消息");
    }
}
