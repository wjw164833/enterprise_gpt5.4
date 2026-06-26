package com.invitation.model.dto.auth;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class WxScanLoginDTO {
    @NotBlank(message = "state不能为空")
    private String state;
}
