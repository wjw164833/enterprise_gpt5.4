package com.invitation.common.enums;

import lombok.Getter;

/**
 * 支付状态枚举
 */
@Getter
public enum PaymentStatus {

    PENDING(0, "待支付"),
    PAID(1, "已支付"),
    REFUNDED(2, "已退款"),
    CLOSED(3, "已关闭");

    private final int code;
    private final String desc;

    PaymentStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static PaymentStatus fromCode(int code) {
        for (PaymentStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid PaymentStatus code: " + code);
    }
}
