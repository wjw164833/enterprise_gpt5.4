package com.invitation.common.constant;

/**
 * 消息队列常量定义
 */
public class MqConstant {

    // Exchange命名规范: exchange.{module}.{action}
    public static final String EXCHANGE_SMS = "exchange.sms.send";
    public static final String EXCHANGE_INVITATION = "exchange.invitation.event";
    public static final String EXCHANGE_VIEW_LOG = "exchange.view.log";
    public static final String EXCHANGE_AI_TASK = "exchange.ai.task";
    public static final String EXCHANGE_PAYMENT = "exchange.payment.event";
    public static final String EXCHANGE_NOTIFICATION = "exchange.notification.send";

    // Queue命名规范: queue.{module}.{action}
    public static final String QUEUE_SMS_SEND = "queue.sms.send";
    public static final String QUEUE_INVITATION_PUBLISH = "queue.invitation.publish";
    public static final String QUEUE_INVITATION_EXPIRE = "queue.invitation.expire";
    public static final String QUEUE_VIEW_LOG_SAVE = "queue.view.log.save";
    public static final String QUEUE_AI_GREETING = "queue.ai.greeting";
    public static final String QUEUE_AI_PHOTO = "queue.ai.photo";
    public static final String QUEUE_PAYMENT_CALLBACK = "queue.payment.callback";
    public static final String QUEUE_PAYMENT_REFUND = "queue.payment.refund";
    public static final String QUEUE_NOTIFICATION_EMAIL = "queue.notification.email";
    public static final String QUEUE_NOTIFICATION_SMS = "queue.notification.sms";

    // 死信队列
    public static final String QUEUE_SMS_SEND_DLQ = "queue.sms.send.dlq";
    public static final String QUEUE_INVITATION_PUBLISH_DLQ = "queue.invitation.publish.dlq";
    public static final String QUEUE_AI_GREETING_DLQ = "queue.ai.greeting.dlq";
    public static final String QUEUE_PAYMENT_CALLBACK_DLQ = "queue.payment.callback.dlq";

    // Routing Key
    public static final String RK_SMS_SEND = "sms.send";
    public static final String RK_INVITATION_PUBLISH = "invitation.publish";
    public static final String RK_INVITATION_EXPIRE = "invitation.expire";
    public static final String RK_VIEW_LOG_SAVE = "view.log.save";
    public static final String RK_AI_GREETING = "ai.greeting";
    public static final String RK_AI_PHOTO = "ai.photo";
    public static final String RK_PAYMENT_CALLBACK = "payment.callback";
    public static final String RK_PAYMENT_REFUND = "payment.refund";
    public static final String RK_NOTIFICATION_EMAIL = "notification.email";
    public static final String RK_NOTIFICATION_SMS = "notification.sms";

    private MqConstant() {}
}
