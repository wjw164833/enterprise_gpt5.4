package com.invitation.common.constant;

/**
 * 业务常量
 */
public class BusinessConstant {

    /** 默认分页大小 */
    public static final int DEFAULT_PAGE_SIZE = 10;
    /** 最大分页大小 */
    public static final int MAX_PAGE_SIZE = 100;
    /** 验证码长度 */
    public static final int SMS_CODE_LENGTH = 6;
    /** 短链码长度 */
    public static final int SHORT_CODE_LENGTH = 8;
    /** 邀请语最大长度 */
    public static final int GREETING_MAX_LENGTH = 500;
    /** 祝福留言最大长度 */
    public static final int BLESS_MAX_LENGTH = 512;
    /** 聊天消息最大长度 */
    public static final int CHAT_MSG_MAX_LENGTH = 1024;
    /** 昵称最大长度 */
    public static final int NICKNAME_MAX_LENGTH = 64;

    /** 允许上传的图片类型 */
    public static final String[] ALLOWED_IMAGE_TYPES = {"jpg", "jpeg", "png", "gif", "webp", "bmp"};
    /** 允许上传的视频类型 */
    public static final String[] ALLOWED_VIDEO_TYPES = {"mp4", "avi", "mov", "wmv"};
    /** 允许上传的音频类型 */
    public static final String[] ALLOWED_AUDIO_TYPES = {"mp3", "wav", "aac", "ogg"};
    /** 图片最大大小(字节) 10MB */
    public static final long IMAGE_MAX_SIZE = 10 * 1024 * 1024;
    /** 视频最大大小(字节) 50MB */
    public static final long VIDEO_MAX_SIZE = 50 * 1024 * 1024;
    /** 音频最大大小(字节) 20MB */
    public static final long AUDIO_MAX_SIZE = 20 * 1024 * 1024;

    /** 免费版邀请函配额 */
    public static final int FREE_INVITATION_QUOTA = 1;
    /** 专业版邀请函配额 */
    public static final int PRO_INVITATION_QUOTA = 20;
    /** 企业版邀请函配额 */
    public static final int ENTERPRISE_INVITATION_QUOTA = 999;
    /** 免费版模板配额 */
    public static final int FREE_TEMPLATE_QUOTA = 3;
    /** 专业版AI配额(次/月) */
    public static final int PRO_AI_QUOTA = 50;
    /** 企业版AI配额(次/月) */
    public static final int ENTERPRISE_AI_QUOTA = 500;

    /** 专业版月费(分) */
    public static final int PRO_MONTHLY_FEE = 2900;
    /** 企业版月费(分) */
    public static final int ENTERPRISE_MONTHLY_FEE = 9900;

    private BusinessConstant() {}
}
