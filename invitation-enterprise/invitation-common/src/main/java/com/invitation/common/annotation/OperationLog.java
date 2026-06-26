package com.invitation.common.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解 - AOP切面记录审计日志
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {

    /** 操作模块 */
    String module() default "";

    /** 操作类型 */
    String action() default "";

    /** 操作描述 */
    String description() default "";

    /** 是否保存请求参数 */
    boolean saveParams() default true;

    /** 是否保存响应结果 */
    boolean saveResult() default false;
}
