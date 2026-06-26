package com.invitation.infra.mq;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;

/**
 * 消息消费基类 - 提供手动ACK/NACK模板
 */
@Slf4j
public abstract class MessageConsumer implements ChannelAwareMessageListener {

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            String body = new String(message.getBody());
            log.info("消费MQ消息: queue={}, deliveryTag={}", message.getMessageProperties().getConsumerQueue(), deliveryTag);
            process(body);
            // 手动确认
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("消费MQ消息失败: deliveryTag={}, error={}", deliveryTag, e.getMessage(), e);
            // 重新入队（最多重试3次）
            boolean requeue = message.getMessageProperties().getRedelivered() == null
                    || !message.getMessageProperties().getRedelivered();
            channel.basicNack(deliveryTag, false, requeue);
        }
    }

    /**
     * 子类实现消息处理逻辑
     */
    protected abstract void process(String messageBody);
}
