package com.invitation.common.util;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 微信小程序工具类
 */
@Slf4j
@Component
public class WxMaUtil {

    @Autowired(required = false)
    private WxMaService wxMaService;

    /**
     * 通过code获取session_key和openid
     */
    public WxMaJscode2SessionResult jsCode2Session(String code) {
        try {
            if (wxMaService == null) {
                log.warn("微信小程序服务未配置");
                return null;
            }
            return wxMaService.getUserService().getSessionInfo(code);
        } catch (WxErrorException e) {
            log.error("微信小程序登录失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取手机号
     */
    public WxMaPhoneNumberInfo getPhoneNumber(String code) {
        try {
            if (wxMaService == null) {
                log.warn("微信小程序服务未配置");
                return null;
            }
            return wxMaService.getUserService().getPhoneNoInfo(code);
        } catch (WxErrorException e) {
            log.error("获取微信手机号失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 生成小程序码
     */
    public byte[] generateQrCode(String scene, String page, int width) {
        try {
            if (wxMaService == null) {
                log.warn("微信小程序服务未配置");
                return null;
            }
            return wxMaService.getQrcodeService()
                    .createWxaCodeUnlimitBytes(scene, page, false, null, width, true, null, false);
        } catch (WxErrorException e) {
            log.error("生成小程序码失败: {}", e.getMessage(), e);
            return null;
        }
    }
}
