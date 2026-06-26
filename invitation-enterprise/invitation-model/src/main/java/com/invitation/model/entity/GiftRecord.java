package com.invitation.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("gift_record")
public class GiftRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long invitationId;
    private Long paymentId;
    private String guestName;
    private String guestPhone;
    private BigDecimal amount;
    private String message;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;
}
