package com.invitation.common.constant;

/**
 * API路径常量
 */
public class ApiConstant {

    /** API版本前缀 */
    public static final String API_PREFIX = "/api/v1";
    /** 认证模块 */
    public static final String AUTH = API_PREFIX + "/auth";
    /** 邀请函模块 */
    public static final String INVITATION = API_PREFIX + "/invitations";
    /** 宾客端模块 */
    public static final String GUEST = API_PREFIX + "/guest";
    /** 留言模块 */
    public static final String BLESS = API_PREFIX + "/bless";
    /** 模板模块 */
    public static final String TEMPLATE = API_PREFIX + "/templates";
    /** 席位模块 */
    public static final String SEAT = API_PREFIX + "/seats";
    /** 聊天模块 */
    public static final String CHAT = API_PREFIX + "/chat";
    /** 支付模块 */
    public static final String PAYMENT = API_PREFIX + "/payment";
    /** 订阅模块 */
    public static final String SUBSCRIPTION = API_PREFIX + "/subscription";
    /** AI模块 */
    public static final String AI = API_PREFIX + "/ai";
    /** 分析模块 */
    public static final String ANALYTICS = API_PREFIX + "/analytics";
    /** 分享模块 */
    public static final String SHARE = API_PREFIX + "/share";
    /** 管理模块 */
    public static final String ADMIN = API_PREFIX + "/admin";
    /** 音乐模块 */
    public static final String MUSIC = API_PREFIX + "/music";
    /** 相册模块 */
    public static final String ALBUM = API_PREFIX + "/album";

    /** 公开接口前缀（无需JWT） */
    public static final String PUBLIC = API_PREFIX + "/public";
    /** WebSocket端点 */
    public static final String WS_ENDPOINT = "/ws/chat";

    /** 登录接口 */
    public static final String AUTH_SMS_SEND = AUTH + "/sms/send";
    public static final String AUTH_SMS_LOGIN = AUTH + "/sms/login";
    public static final String AUTH_WX_MINI_LOGIN = AUTH + "/wx/mini/login";

    private ApiConstant() {}
}
