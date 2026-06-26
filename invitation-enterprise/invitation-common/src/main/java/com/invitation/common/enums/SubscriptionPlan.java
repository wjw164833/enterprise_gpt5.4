package com.invitation.common.enums;

import lombok.Getter;

/**
 * 订阅计划枚举
 */
@Getter
public enum SubscriptionPlan {

    FREE(1, "免费版"),
    PROFESSIONAL(2, "专业版"),
    ENTERPRISE(3, "企业版");

    private final int code;
    private final String desc;

    SubscriptionPlan(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static SubscriptionPlan fromCode(int code) {
        for (SubscriptionPlan plan : values()) {
            if (plan.code == code) {
                return plan;
            }
        }
        throw new IllegalArgumentException("Invalid SubscriptionPlan code: " + code);
    }
}
