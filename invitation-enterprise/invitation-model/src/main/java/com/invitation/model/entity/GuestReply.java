package com.invitation.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("guest_reply")
public class GuestReply {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long invitationId;
    private String guestName;
    private String guestPhone;
    private Integer replyStatus;
    private Integer guestCount;
    private String remark;
    private String ipAddress;
    private String userAgent;
    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatedAt;
}
