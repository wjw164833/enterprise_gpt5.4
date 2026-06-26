package com.invitation.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("bless_message")
public class BlessMessage {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long invitationId;
    private String guestName;
    private String guestAvatar;
    private String content;
    private String theme;
    private String ipAddress;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;
    @TableLogic
    private Integer deleted;
}
