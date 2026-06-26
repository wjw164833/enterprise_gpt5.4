package com.invitation.service.auth;

import com.invitation.common.model.R;
import com.invitation.model.dto.auth.SmsLoginDTO;
import com.invitation.model.dto.auth.TokenDTO;
import com.invitation.model.dto.auth.WxLoginDTO;

/**
 * 认证服务接口
 */
public interface AuthService {
    /** 发送短信验证码 */
    R<Void> sendSmsCode(String phone, String ip);
    /** 手机号验证码登录 */
    TokenDTO smsLogin(SmsLoginDTO dto, String ip);
    /** 微信小程序登录 */
    TokenDTO wxMiniLogin(WxLoginDTO dto, String ip);
    /** 微信扫码登录-获取二维码 */
    java.util.Map<String, String> getWxScanQrCode();
    /** 微信扫码登录-轮询状态 */
    TokenDTO checkWxScanStatus(String state);
    /** 微信扫码回调 */
    String wxScanCallback(String code, String state);
    /** 刷新Token */
    TokenDTO refreshToken(String refreshToken);
    /** 退出登录 */
    R<Void> logout(String token);
}
