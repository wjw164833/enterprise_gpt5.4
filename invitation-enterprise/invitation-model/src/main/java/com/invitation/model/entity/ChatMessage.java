package com.invitation.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("chat_message")
public class ChatMessage {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long invitationId;
    private Long userId;
    private String guestName;
    private String content;
    private Integer messageType;
    private Integer isRead;
    private Integer isRevoked;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
