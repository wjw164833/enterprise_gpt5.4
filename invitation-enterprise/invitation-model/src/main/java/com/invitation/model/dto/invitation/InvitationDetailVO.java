package com.invitation.model.dto.invitation;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class InvitationDetailVO {
    private Long id;
    private Long userId;
    private String title;
    private Integer activityType;
    private Long templateId;
    private String coverImage;
    private String content;
    private Date activityDate;
    private String activityAddress;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Long musicId;
    private String customMusicUrl;
    private String videoUrl;
    private String aiGreeting;
    private String extraData;
    private String ogTitle;
    private String ogDescription;
    private String ogImage;
    private String shortCode;
    private String qrcodeUrl;
    private String posterUrl;
    private Integer pv;
    private Integer uv;
    private Integer replyCount;
    private Integer attendCount;
    private Integer blessCount;
    private BigDecimal giftAmount;
    private Integer status;
    private Integer watermark;
    private Date publishedAt;
    private Date createdAt;
    private Date updatedAt;
    private String templateName;
    private String templateStyle;
    private String musicName;
    private String musicUrl;
}
