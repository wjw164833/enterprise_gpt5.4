package com.invitation.common.annotation;

import java.lang.annotation.*;

/**
 * 限流注解 - 基于Sentinel实现
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    /** 资源名称 */
    String value() default "";

    /** QPS限流阈值 */
    double count() default 100;

    /** 限流模式: 0直接拒绝 1匀速排队 2预热 */
    int mode() default 0;

    /** 预热时长(秒), mode=2时生效 */
    int warmupSec() default 10;

    /** 排队等待超时(毫秒), mode=1时生效 */
    int queueTimeout() default 500;
}
