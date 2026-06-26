package com.invitation.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("template")
public class Template {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String coverImage;
    private String previewImages;
    private Integer templateType;
    private String style;
    private String config;
    private Integer sortOrder;
    private Integer isFree;
    private Integer minPlan;
    private Integer usageCount;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatedAt;
    @TableLogic
    private Integer deleted;
}
