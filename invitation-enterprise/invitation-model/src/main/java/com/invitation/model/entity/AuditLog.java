package com.invitation.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("audit_log")
public class AuditLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String username;
    private String module;
    private String action;
    private String targetType;
    private Long targetId;
    private String detail;
    private String ipAddress;
    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;
}
