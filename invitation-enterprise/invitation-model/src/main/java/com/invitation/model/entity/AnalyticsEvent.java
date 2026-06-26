package com.invitation.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("analytics_event")
public class AnalyticsEvent {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long invitationId;
    private Long userId;
    private String eventType;
    private String eventName;
    private String eventData;
    private String sessionId;
    private String platform;
    private String ipAddress;
    private String userAgent;
    private String extraData;
    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;
}
