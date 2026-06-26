package com.invitation.infra.mq;

import com.invitation.common.constant.MqConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * RabbitMQ配置 - Exchange/Queue/Binding定义
 */
@Slf4j
@Configuration
public class RabbitMqConfig {

    // =================== 消息转换器 ===================

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        template.setMandatory(true);
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setPrefetchCount(10);
        return factory;
    }

    // =================== SMS ===================

    @Bean
    public DirectExchange smsExchange() {
        return new DirectExchange(MqConstant.EXCHANGE_SMS, true, false);
    }

    @Bean
    public Queue smsQueue() {
        Map<String, Object> args = new HashMap<>(4);
        args.put("x-dead-letter-exchange", MqConstant.EXCHANGE_SMS);
        args.put("x-dead-letter-routing-key", "dlq." + MqConstant.RK_SMS_SEND);
        return QueueBuilder.durable(MqConstant.QUEUE_SMS_SEND).withArguments(args).build();
    }

    @Bean
    public Queue smsDlq() {
        return QueueBuilder.durable(MqConstant.QUEUE_SMS_SEND_DLQ).build();
    }

    @Bean
    public Binding smsDlqBinding() {
        return BindingBuilder.bind(smsDlq()).to(smsExchange()).with("dlq." + MqConstant.RK_SMS_SEND);
    }

    @Bean
    public Binding smsBinding() {
        return BindingBuilder.bind(smsQueue()).to(smsExchange()).with(MqConstant.RK_SMS_SEND);
    }

    // =================== 邀请函事件 ===================

    @Bean
    public DirectExchange invitationExchange() {
        return new DirectExchange(MqConstant.EXCHANGE_INVITATION, true, false);
    }

    @Bean
    public Queue invitationPublishQueue() {
        return QueueBuilder.durable(MqConstant.QUEUE_INVITATION_PUBLISH).build();
    }

    @Bean
    public Queue invitationExpireQueue() {
        return QueueBuilder.durable(MqConstant.QUEUE_INVITATION_EXPIRE).build();
    }

    @Bean
    public Binding invitationPublishBinding() {
        return BindingBuilder.bind(invitationPublishQueue()).to(invitationExchange()).with(MqConstant.RK_INVITATION_PUBLISH);
    }

    @Bean
    public Binding invitationExpireBinding() {
        return BindingBuilder.bind(invitationExpireQueue()).to(invitationExchange()).with(MqConstant.RK_INVITATION_EXPIRE);
    }

    // =================== 浏览日志 ===================

    @Bean
    public DirectExchange viewLogExchange() {
        return new DirectExchange(MqConstant.EXCHANGE_VIEW_LOG, true, false);
    }

    @Bean
    public Queue viewLogQueue() {
        return QueueBuilder.durable(MqConstant.QUEUE_VIEW_LOG_SAVE).build();
    }

    @Bean
    public Binding viewLogBinding() {
        return BindingBuilder.bind(viewLogQueue()).to(viewLogExchange()).with(MqConstant.RK_VIEW_LOG_SAVE);
    }

    // =================== AI任务 ===================

    @Bean
    public DirectExchange aiTaskExchange() {
        return new DirectExchange(MqConstant.EXCHANGE_AI_TASK, true, false);
    }

    @Bean
    public Queue aiGreetingQueue() {
        return QueueBuilder.durable(MqConstant.QUEUE_AI_GREETING).build();
    }

    @Bean
    public Queue aiPhotoQueue() {
        return QueueBuilder.durable(MqConstant.QUEUE_AI_PHOTO).build();
    }

    @Bean
    public Queue aiGreetingDlq() {
        return QueueBuilder.durable(MqConstant.QUEUE_AI_GREETING_DLQ).build();
    }

    @Bean
    public Binding aiGreetingDlqBinding() {
        return BindingBuilder.bind(aiGreetingDlq()).to(aiTaskExchange()).with("dlq." + MqConstant.RK_AI_GREETING);
    }

    @Bean
    public Binding aiGreetingBinding() {
        return BindingBuilder.bind(aiGreetingQueue()).to(aiTaskExchange()).with(MqConstant.RK_AI_GREETING);
    }

    @Bean
    public Binding aiPhotoBinding() {
        return BindingBuilder.bind(aiPhotoQueue()).to(aiTaskExchange()).with(MqConstant.RK_AI_PHOTO);
    }

    // =================== 支付事件 ===================

    @Bean
    public DirectExchange paymentExchange() {
        return new DirectExchange(MqConstant.EXCHANGE_PAYMENT, true, false);
    }

    @Bean
    public Queue paymentCallbackQueue() {
        return QueueBuilder.durable(MqConstant.QUEUE_PAYMENT_CALLBACK).build();
    }

    @Bean
    public Queue paymentRefundQueue() {
        return QueueBuilder.durable(MqConstant.QUEUE_PAYMENT_REFUND).build();
    }

    @Bean
    public Queue paymentCallbackDlq() {
        return QueueBuilder.durable(MqConstant.QUEUE_PAYMENT_CALLBACK_DLQ).build();
    }

    @Bean
    public Binding paymentCallbackDlqBinding() {
        return BindingBuilder.bind(paymentCallbackDlq()).to(paymentExchange()).with("dlq." + MqConstant.RK_PAYMENT_CALLBACK);
    }

    @Bean
    public Binding paymentCallbackBinding() {
        return BindingBuilder.bind(paymentCallbackQueue()).to(paymentExchange()).with(MqConstant.RK_PAYMENT_CALLBACK);
    }

    @Bean
    public Binding paymentRefundBinding() {
        return BindingBuilder.bind(paymentRefundQueue()).to(paymentExchange()).with(MqConstant.RK_PAYMENT_REFUND);
    }

    // =================== 通知 ===================

    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange(MqConstant.EXCHANGE_NOTIFICATION, true, false);
    }

    @Bean
    public Queue notificationEmailQueue() {
        return QueueBuilder.durable(MqConstant.QUEUE_NOTIFICATION_EMAIL).build();
    }

    @Bean
    public Queue notificationSmsQueue() {
        return QueueBuilder.durable(MqConstant.QUEUE_NOTIFICATION_SMS).build();
    }

    @Bean
    public Binding notificationEmailBinding() {
        return BindingBuilder.bind(notificationEmailQueue()).to(notificationExchange()).with(MqConstant.RK_NOTIFICATION_EMAIL);
    }

    @Bean
    public Binding notificationSmsBinding() {
        return BindingBuilder.bind(notificationSmsQueue()).to(notificationExchange()).with(MqConstant.RK_NOTIFICATION_SMS);
    }
}
