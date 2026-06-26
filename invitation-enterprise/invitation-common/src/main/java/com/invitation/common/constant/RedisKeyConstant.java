package com.invitation.common.constant;

/**
 * Redis Key常量定义
 */
public class RedisKeyConstant {

    /** 验证码 - sms:code:{phone} */
    public static final String SMS_CODE = "sms:code:";
    /** 验证码发送限流 - sms:limit:{phone} */
    public static final String SMS_LIMIT = "sms:limit:";
    /** 验证码IP限流 - sms:ip:limit:{ip} */
    public static final String SMS_IP_LIMIT = "sms:ip:limit:";
    /** 用户Session - session:{token} */
    public static final String SESSION = "session:";
    /** 刷新Token - refresh:{userId} */
    public static final String REFRESH_TOKEN = "refresh:";
    /** 邀请函详情缓存 - inv:detail:{id} */
    public static final String INVITATION_DETAIL = "inv:detail:";
    /** 邀请函短链映射 - inv:short:{shortCode} */
    public static final String INVITATION_SHORT_CODE = "inv:short:";
    /** 模板详情缓存 - tpl:detail:{id} */
    public static final String TEMPLATE_DETAIL = "tpl:detail:";
    /** 模板列表缓存 - tpl:list:{type}:{style} */
    public static final String TEMPLATE_LIST = "tpl:list:";
    /** PV HyperLogLog - pv:{invitationId}:{date} */
    public static final String PV = "pv:";
    /** UV HyperLogLog - uv:{invitationId}:{date} */
    public static final String UV = "uv:";
    /** PV计数器 - pv:count:{invitationId} */
    public static final String PV_COUNT = "pv:count:";
    /** UV计数器 - uv:count:{invitationId} */
    public static final String UV_COUNT = "uv:count:";
    /** 邀请函PV计数兼容前缀 */
    public static final String INVITATION_PV_COUNT = PV_COUNT;
    /** 邀请函UV计数兼容前缀 */
    public static final String INVITATION_UV_COUNT = UV_COUNT;
    /** 分布式锁 - lock:poster:{invitationId} */
    public static final String LOCK_POSTER = "lock:poster:";
    /** 分布式锁 - lock:seat:{tableId} */
    public static final String LOCK_SEAT = "lock:seat:";
    /** 分布式锁 - lock:payment:{orderNo} */
    public static final String LOCK_PAYMENT = "lock:payment:";
    /** 支付锁兼容前缀 */
    public static final String PAYMENT_LOCK = LOCK_PAYMENT;
    /** 限流计数 - rate:{api}:{identifier} */
    public static final String RATE_LIMIT = "rate:";
    /** 微信扫码状态 - wx:scan:{state} */
    public static final String WX_SCAN_STATE = "wx:scan:";
    /** 订阅信息缓存 - sub:info:{userId} */
    public static final String SUBSCRIPTION_INFO = "sub:info:";
    /** AI任务状态 - ai:task:{taskId} */
    public static final String AI_TASK = "ai:task:";
    /** AI额度计数 - ai:quota:{userId} */
    public static final String AI_QUOTA = "ai:quota:";
    /** 分析事件计数 - analytics:event:{invitationId}:{eventName} */
    public static final String ANALYTICS_EVENT_COUNT = "analytics:event:";
    /** 聊天室在线用户 - chat:online:{invitationId} */
    public static final String CHAT_ONLINE = "chat:online:";

    /** 验证码TTL(秒) */
    public static final long SMS_CODE_TTL = 300;
    /** 验证码限流TTL(秒) */
    public static final long SMS_LIMIT_TTL = 60;
    /** IP限流TTL(秒) */
    public static final long SMS_IP_LIMIT_TTL = 3600;
    /** Session TTL(秒) */
    public static final long SESSION_TTL = 7200;
    /** 详情缓存TTL(秒) */
    public static final long DETAIL_CACHE_TTL = 1800;
    /** 列表缓存TTL(秒) */
    public static final long LIST_CACHE_TTL = 600;
    /** 海报锁TTL(秒) */
    public static final long LOCK_POSTER_TTL = 30;
    /** 席位锁TTL(秒) */
    public static final long LOCK_SEAT_TTL = 10;
    /** 支付锁TTL(秒) */
    public static final long LOCK_PAYMENT_TTL = 30;

    private RedisKeyConstant() {}
}
