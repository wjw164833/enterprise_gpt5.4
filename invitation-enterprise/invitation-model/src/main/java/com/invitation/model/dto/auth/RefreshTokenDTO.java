package com.invitation.model.dto.auth;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class RefreshTokenDTO {
    @NotBlank(message = "refreshToken不能为空")
    private String refreshToken;
}
