package com.invitation.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("short_link")
public class ShortLink {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String shortCode;
    private String originalUrl;
    private Long invitationId;
    private Integer clickCount;
    private Date expiredAt;
    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;
}
