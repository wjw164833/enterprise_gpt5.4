package com.invitation.web.controller;

import com.invitation.common.annotation.RateLimit;
import com.invitation.common.model.R;
import com.invitation.model.dto.auth.RefreshTokenDTO;
import com.invitation.model.dto.auth.SmsCodeDTO;
import com.invitation.model.dto.auth.SmsLoginDTO;
import com.invitation.model.dto.auth.TokenDTO;
import com.invitation.model.dto.auth.WxLoginDTO;
import com.invitation.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 认证控制器
 */
@Tag(name = "认证管理")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "发送短信验证码")
    @PostMapping("/sms/code")
    @RateLimit(value = "sms_code", count = 1, warmupSec = 60)
    public R<Void> sendSmsCode(@Validated @RequestBody SmsCodeDTO dto, HttpServletRequest request) {
        // P1-14: 改为@RequestBody接收JSON body
        String ip = request.getRemoteAddr();
        return authService.sendSmsCode(dto.getPhone(), ip);
    }

    @Operation(summary = "短信验证码登录")
    @PostMapping("/sms/login")
    public R<TokenDTO> smsLogin(@Validated @RequestBody SmsLoginDTO dto, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        return R.ok(authService.smsLogin(dto, ip));
    }

    @Operation(summary = "微信小程序登录")
    @PostMapping("/wx/mini/login")
    public R<TokenDTO> wxMiniLogin(@Validated @RequestBody WxLoginDTO dto, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        return R.ok(authService.wxMiniLogin(dto, ip));
    }

    @Operation(summary = "微信扫码登录URL")
    @GetMapping("/wx/scan/url")
    public R<Map<String, String>> getWxScanUrl(HttpServletRequest request) {
        String callbackUrl = request.getScheme() + "://" + request.getServerName()
                + "/api/v1/auth/wx/scan/callback";
        Map<String, String> result = authService.getWxScanQrCode();
        return R.ok(result);
    }

    @Operation(summary = "微信扫码登录轮询")
    @GetMapping("/wx/scan/poll")
    public R<TokenDTO> wxScanPoll(@RequestParam String state) {
        TokenDTO tokenDTO = authService.checkWxScanStatus(state);
        return R.ok(tokenDTO);
    }

    @Operation(summary = "刷新Token")
    @PostMapping("/refresh")
    public R<TokenDTO> refreshToken(@Validated @RequestBody RefreshTokenDTO dto) {
        return R.ok(authService.refreshToken(dto.getRefreshToken()));
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public R<Void> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return authService.logout(token);
    }
}
