package com.invitation.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("invitation")
public class Invitation {
    @TableId(type = IdType.AUTO)
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
    private String videoCoverUrl;
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
    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatedAt;
    @TableLogic
    private Integer deleted;
}
