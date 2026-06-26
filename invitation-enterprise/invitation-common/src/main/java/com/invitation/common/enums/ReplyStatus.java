package com.invitation.common.enums;

import lombok.Getter;

/**
 * 宾客回复状态枚举
 */
@Getter
public enum ReplyStatus {

    ATTEND(1, "出席"),
    UNCERTAIN(2, "不确定"),
    NOT_ATTEND(3, "不出席");

    private final int code;
    private final String desc;

    ReplyStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ReplyStatus fromCode(int code) {
        for (ReplyStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid ReplyStatus code: " + code);
    }
}
