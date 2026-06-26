package com.invitation.common.enums;

import lombok.Getter;

/**
 * 活动类型枚举
 */
@Getter
public enum EventType {

    WEDDING(1, "婚礼"),
    BIRTHDAY(2, "生日"),
    BUSINESS(3, "商务"),
    SCHOOL(4, "升学"),
    OTHER(5, "其他");

    private final int code;
    private final String desc;

    EventType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static EventType fromCode(int code) {
        for (EventType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid EventType code: " + code);
    }
}
