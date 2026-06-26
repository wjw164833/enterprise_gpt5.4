package com.invitation.model.dto.share;

import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class PosterGenerateDTO {
    @NotNull(message = "邀请函ID不能为空")
    private Long invitationId;
    private Integer width = 750;
    private Integer height = 1334;
}
