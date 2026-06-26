package com.invitation.model.dto.auth;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * P1-14: 短信验证码发送请求DTO
 * 前端发送JSON body，后端使用@RequestBody接收
 */
@Data
public class SmsCodeDTO {
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
}
