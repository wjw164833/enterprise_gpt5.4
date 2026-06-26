package com.invitation.model.dto.template;

import lombok.Data;

@Data
public class TemplateQueryDTO {
    private Integer templateType;
    private String style;
    private Integer isFree;
    private Integer page = 1;
    private Integer size = 20;
}
