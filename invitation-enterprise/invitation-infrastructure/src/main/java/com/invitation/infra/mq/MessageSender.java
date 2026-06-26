package com.invitation.infra.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 消息发送服务 - 封装RabbitMQ消息发送
 */
@Slf4j
@Component
public class MessageSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息到指定Exchange和RoutingKey
     */
    public void send(String exchange, String routingKey, Object message) {
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        log.info("发送MQ消息: exchange={}, routingKey={}, correlationId={}", exchange, routingKey, correlationData.getId());
        rabbitTemplate.convertAndSend(exchange, routingKey, message, correlationData);
    }

    /**
     * 发送延迟消息（通过TTL+死信队列实现）
     */
    public void sendDelay(String exchange, String routingKey, Object message, long delayMillis) {
        // 延迟消息通过设置消息TTL实现
        rabbitTemplate.convertAndSend(exchange, routingKey, message, msg -> {
            msg.getMessageProperties().setExpiration(String.valueOf(delayMillis));
            return msg;
        });
        log.info("发送延迟MQ消息: exchange={}, routingKey={}, delay={}ms", exchange, routingKey, delayMillis);
    }
}
