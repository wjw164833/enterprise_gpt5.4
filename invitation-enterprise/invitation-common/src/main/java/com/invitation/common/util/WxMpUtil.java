package com.invitation.common.util;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信公众号工具类
 */
@Slf4j
@Component
public class WxMpUtil {

    private WxMpService wxMpService;

    @Autowired(required = false)
    public void setWxMpService(WxMpService wxMpService) {
        this.wxMpService = wxMpService;
    }

    /**
     * 通过code获取网页授权AccessToken
     */
    public WxOAuth2AccessToken oauth2getAccessToken(String code) {
        try {
            if (wxMpService == null) {
                log.warn("微信公众号服务未配置");
                return null;
            }
            return wxMpService.getOAuth2Service().getAccessToken(code);
        } catch (WxErrorException e) {
            log.error("获取网页授权AccessToken失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取微信用户信息
     */
    public WxOAuth2UserInfo getUserInfo(WxOAuth2AccessToken oAuth2AccessToken, String lang) {
        try {
            if (wxMpService == null) {
                return null;
            }
            return wxMpService.getOAuth2Service().getUserInfo(oAuth2AccessToken, lang);
        } catch (WxErrorException e) {
            log.error("获取微信用户信息失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 构造网页授权URL
     */
    public String buildOAuth2Url(String redirectUri, String state, String scope) {
        if (wxMpService == null) {
            return "";
        }
        return wxMpService.getOAuth2Service().buildAuthorizationUrl(redirectUri, scope, state);
    }

    /**
     * 获取公众号JS-SDK签名配置
     */
    public Map<String, Object> getJsSdkConfig(String url) {
        if (wxMpService == null) {
            log.warn("微信公众号服务未配置");
            return new HashMap<>();
        }
        try {
            WxJsapiSignature signature = wxMpService.createJsapiSignature(url);
            Map<String, Object> config = new HashMap<>(8);
            config.put("appId", signature.getAppId());
            config.put("timestamp", signature.getTimestamp());
            config.put("nonceStr", signature.getNonceStr());
            config.put("signature", signature.getSignature());
            config.put("url", signature.getUrl());
            return config;
        } catch (WxErrorException e) {
            log.error("获取JS-SDK签名失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }
}
