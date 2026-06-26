package com.invitation.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("sms_log")
public class SmsLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String phone;
    private String code;
    private String bizType;
    private Integer status;
    private String msgId;
    private String errorMsg;
    private String ipAddress;
    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;
}
