package com.invitation.web.controller;

import com.invitation.common.model.R;
import com.invitation.common.util.WxMaUtil;
import com.invitation.common.util.WxMpUtil;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 微信授权控制器
 * P1-09: 移除公开暴露小程序access_token的端点
 */
@Tag(name = "微信授权")
@Slf4j
@RestController
@RequestMapping("/api/v1/wx")
@RequiredArgsConstructor
public class WxAuthController {

    private final WxMaUtil wxMaUtil;
    private final WxMpUtil wxMpUtil;

    @Value("${wx.mini.appid:}")
    private String miniAppId;

    @Value("${wx.mp.appid:}")
    private String mpAppId;

    @Operation(summary = "获取小程序手机号")
    @PostMapping("/mini/phone")
    public R<String> getMiniPhone(@RequestParam String code) {
        try {
            WxMaPhoneNumberInfo phoneInfo = wxMaUtil.getPhoneNumber(code);
            String phone = phoneInfo != null ? phoneInfo.getPhoneNumber() : null;
            return R.ok(phone);
        } catch (Exception e) {
            log.error("获取小程序手机号失败", e);
            return R.fail("获取手机号失败");
        }
    }

    @Operation(summary = "获取公众号JS-SDK签名")
    @GetMapping("/mp/jssdk/config")
    public R<Map<String, Object>> getJsSdkConfig(@RequestParam String url) {
        try {
            Map<String, Object> config = wxMpUtil.getJsSdkConfig(url);
            return R.ok(config);
        } catch (Exception e) {
            log.error("获取JS-SDK配置失败", e);
            return R.fail("获取JS-SDK配置失败");
        }
    }

    // P1-09: 已删除 GET /mini/access-token 公开端点
    // access_token 是敏感凭证，不应公开暴露
    // 如需内部获取，请通过 WxMaUtil 直接调用
}
