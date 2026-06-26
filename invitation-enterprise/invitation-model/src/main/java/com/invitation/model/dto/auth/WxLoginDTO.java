package com.invitation.model.dto.auth;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class WxLoginDTO {
    @NotBlank(message = "微信code不能为空")
    private String code;

    private String encryptedData;
    private String iv;
}
