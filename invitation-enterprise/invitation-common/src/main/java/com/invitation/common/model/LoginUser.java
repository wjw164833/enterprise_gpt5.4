package com.invitation.common.model;

import lombok.Data;
import java.io.Serializable;

/**
 * 登录用户上下文 - 存储在ThreadLocal中的当前登录用户信息
 */
@Data
public class LoginUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;
    private String nickname;
    private String avatar;
    private Integer userType;
    private String phone;
    private String token;

    public LoginUser() {}

    public LoginUser(Long userId, String nickname, String avatar, Integer userType, String phone) {
        this.userId = userId;
        this.nickname = nickname;
        this.avatar = avatar;
        this.userType = userType;
        this.phone = phone;
    }

    private static final ThreadLocal<LoginUser> CONTEXT = new ThreadLocal<>();

    public static void set(LoginUser user) {
        CONTEXT.set(user);
    }

    public static LoginUser get() {
        return CONTEXT.get();
    }

    public static Long getUserId() {
        LoginUser user = CONTEXT.get();
        return user != null ? user.getUserId() : null;
    }

    public static void remove() {
        CONTEXT.remove();
    }

    public boolean isEnterprise() {
        return userType != null && userType == 2;
    }

    public boolean isAdmin() {
        return userType != null && userType == 3;
    }
}
