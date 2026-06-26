package com.invitation.common.enums;

import lombok.Getter;

/**
 * 用户类型枚举
 */
@Getter
public enum UserTypeEnum {

    NORMAL(1, "普通用户"),
    ENTERPRISE(2, "企业用户"),
    ADMIN(3, "管理员");

    private final int code;
    private final String desc;

    UserTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static UserTypeEnum fromCode(int code) {
        for (UserTypeEnum type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid UserTypeEnum code: " + code);
    }
}
