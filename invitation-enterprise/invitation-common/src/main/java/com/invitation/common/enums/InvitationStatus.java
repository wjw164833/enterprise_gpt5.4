package com.invitation.common.enums;

import lombok.Getter;

/**
 * 邀请函状态枚举
 */
@Getter
public enum InvitationStatus {

    DRAFT(0, "草稿"),
    PUBLISHED(1, "已发布"),
    UNPUBLISHED(2, "已下架");

    private final int code;
    private final String desc;

    InvitationStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static InvitationStatus fromCode(int code) {
        for (InvitationStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid InvitationStatus code: " + code);
    }
}
