package com.invitation.common.enums;

import lombok.Getter;

/**
 * 统一返回码枚举
 */
@Getter
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),

    // 认证相关 1xxx
    UNAUTHORIZED(1001, "未认证，请先登录"),
    TOKEN_EXPIRED(1002, "Token已过期"),
    TOKEN_INVALID(1003, "Token无效"),
    REFRESH_TOKEN_EXPIRED(1004, "RefreshToken已过期"),
    SMS_CODE_EXPIRED(1010, "验证码已过期"),
    SMS_CODE_INVALID(1011, "验证码错误"),
    SMS_SEND_LIMIT(1012, "验证码发送过于频繁"),
    WX_LOGIN_FAIL(1020, "微信登录失败"),
    WX_BIND_FAIL(1021, "微信绑定失败"),

    // 权限相关 2xxx
    ACCESS_DENIED(2001, "无权限访问"),
    DATA_SCOPE_DENIED(2002, "无数据权限"),
    RATE_LIMIT_EXCEEDED(2003, "请求过于频繁，请稍后重试"),
    PERMISSION_DENIED(2004, "权限不足"),
    OPERATION_TOO_FREQUENT(2005, "操作过于频繁"),

    // 业务相关 3xxx
    USER_NOT_FOUND(3001, "用户不存在"),
    USER_DISABLED(3002, "用户已被禁用"),
    INVITATION_NOT_FOUND(3101, "邀请函不存在"),
    INVITATION_STATUS_ERROR(3102, "邀请函状态异常"),
    INVITATION_QUOTA_EXCEEDED(3103, "邀请函配额已用完"),
    TEMPLATE_NOT_FOUND(3201, "模板不存在"),
    TEMPLATE_NOT_AVAILABLE(3202, "模板不可用"),
    GUEST_REPLY_DUPLICATE(3301, "您已回复过该邀请函"),
    SEAT_ALREADY_ASSIGNED(3401, "该座位已被分配"),
    SEAT_COUNT_INVALID(3402, "座位数无效"),
    SEAT_TABLE_HAS_GUESTS(3403, "席位表有已分配的宾客"),
    SEAT_TABLE_FULL(3404, "席位表已满"),
    GUEST_ALREADY_SEATED(3405, "该宾客已分配座位"),
    SEAT_TABLE_NOT_FOUND(3406, "席位表不存在"),
    PAYMENT_ORDER_NOT_FOUND(3501, "支付订单不存在"),
    PAYMENT_AMOUNT_ERROR(3502, "支付金额错误"),
    PAYMENT_STATUS_ERROR(3503, "支付状态异常"),
    PAYMENT_ORDER_EXISTS(3504, "支付订单已存在"),
    PAYMENT_STATUS_INVALID(3505, "支付状态无效"),
    SUBSCRIPTION_EXPIRED(3601, "订阅已过期"),
    SUBSCRIPTION_PLAN_ERROR(3602, "订阅计划错误"),
    SUBSCRIPTION_NOT_FOUND(3603, "订阅不存在"),
    AI_QUOTA_EXCEEDED(3701, "AI使用配额已用完"),
    SENSITIVE_WORD_DETECTED(3801, "内容包含敏感词"),
    MESSAGE_REVOKE_EXPIRED(3901, "消息撤回时间已过"),
    PHOTO_LIMIT_EXCEEDED(3910, "照片数量超出限制"),

    // 数据相关
    DATA_NOT_FOUND(3003, "数据不存在"),

    // 系统相关 4xxx
    PARAM_ERROR(4001, "参数错误"),
    FILE_UPLOAD_ERROR(4101, "文件上传失败"),
    FILE_TYPE_NOT_ALLOWED(4102, "文件类型不允许"),
    FILE_SIZE_EXCEEDED(4103, "文件大小超限"),
    OSS_ERROR(4104, "对象存储服务异常"),
    MQ_SEND_ERROR(4201, "消息发送失败"),
    REDIS_ERROR(4301, "缓存服务异常"),
    DB_ERROR(4401, "数据库操作异常");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
