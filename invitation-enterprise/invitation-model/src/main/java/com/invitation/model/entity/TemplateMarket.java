package com.invitation.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("template_market")
public class TemplateMarket {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long designerId;
    private Long templateId;
    private java.math.BigDecimal price;
    private java.math.BigDecimal commissionRate;
    private Integer downloadCount;
    private java.math.BigDecimal rating;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatedAt;
    @TableLogic
    private Integer deleted;
}
