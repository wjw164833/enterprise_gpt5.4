package com.invitation.common.enums;

import lombok.Getter;

/**
 * 模板类型枚举
 */
@Getter
public enum TemplateType {

    WEDDING(1, "婚礼"),
    BIRTHDAY(2, "生日"),
    BUSINESS(3, "商务"),
    GENERAL(4, "通用");

    private final int code;
    private final String desc;

    TemplateType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static TemplateType fromCode(int code) {
        for (TemplateType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid TemplateType code: " + code);
    }
}
