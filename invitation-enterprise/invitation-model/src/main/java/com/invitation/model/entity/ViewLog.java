package com.invitation.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("view_log")
public class ViewLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long invitationId;
    private String visitorId;
    private String ipAddress;
    private String userAgent;
    private String referer;
    private Date viewDate;
    private Integer viewHour;
    private String source;
    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;
}
