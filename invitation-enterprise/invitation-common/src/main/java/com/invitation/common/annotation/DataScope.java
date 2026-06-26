package com.invitation.common.annotation;

import java.lang.annotation.*;

/**
 * 数据权限注解 - 行级数据隔离
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope {

    /** 表别名 */
    String alias() default "t";

    /** 用户ID字段名 */
    String userIdField() default "user_id";
}
