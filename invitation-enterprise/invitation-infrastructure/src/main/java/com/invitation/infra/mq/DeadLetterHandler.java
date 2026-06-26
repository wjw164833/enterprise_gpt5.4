package com.invitation.infra.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.invitation.common.constant.MqConstant;

/**
 * 死信处理器 - 处理消费失败的消息
 */
@Slf4j
@Component
public class DeadLetterHandler {

    /**
     * 处理SMS死信
     */
    @RabbitListener(queues = MqConstant.QUEUE_SMS_SEND_DLQ)
    public void handleSmsDlq(Message message) {
        String body = new String(message.getBody());
        log.error("SMS死信消息: {}", body);
        // 告警、记录、人工处理
    }

    /**
     * 处理AI任务死信
     */
    @RabbitListener(queues = MqConstant.QUEUE_AI_GREETING_DLQ)
    public void handleAiDlq(Message message) {
        String body = new String(message.getBody());
        log.error("AI任务死信消息: {}", body);
    }

    /**
     * 处理支付回调死信
     */
    @RabbitListener(queues = MqConstant.QUEUE_PAYMENT_CALLBACK_DLQ)
    public void handlePaymentDlq(Message message) {
        String body = new String(message.getBody());
        log.error("支付回调死信消息: {}", body);
    }
}
