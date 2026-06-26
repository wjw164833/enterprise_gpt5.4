package com.invitation.model.dto.template;

import lombok.Data;

@Data
public class TemplateVO {
    private Long id;
    private String name;
    private String coverImage;
    private String previewImages;
    private Integer templateType;
    private String style;
    private String config;
    private Integer isFree;
    private Integer minPlan;
    private Integer usageCount;
}
