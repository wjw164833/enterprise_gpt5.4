package com.invitation.model.dto.invitation;

import lombok.Data;

@Data
public class InvitationQueryDTO {
    private Integer activityType;
    private Integer status;
    private String keyword;
    private Integer page = 1;
    private Integer size = 10;
}
