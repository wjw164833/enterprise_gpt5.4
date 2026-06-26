package com.invitation.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * UserAgent解析工具
 */
@Slf4j
@Component
public class UserAgentUtil {

    /**
     * 获取User-Agent字符串
     */
    public String getUserAgent() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return "";
        }
        return request.getHeader("User-Agent");
    }

    /**
     * 判断是否为微信浏览器
     */
    public boolean isWechat(String ua) {
        return ua != null && ua.toLowerCase().contains("micromessenger");
    }

    /**
     * 判断是否为移动端
     */
    public boolean isMobile(String ua) {
        if (ua == null) {
            return false;
        }
        String lower = ua.toLowerCase();
        return lower.contains("android") || lower.contains("iphone")
                || lower.contains("ipad") || lower.contains("ipod")
                || lower.contains("windows phone") || lower.contains("mobile");
    }

    /**
     * 解析平台类型
     */
    public String parsePlatform(String ua) {
        if (ua == null) {
            return "unknown";
        }
        String lower = ua.toLowerCase();
        if (lower.contains("miniprogram") || lower.contains("miniapp")) {
            return "miniprogram";
        }
        if (lower.contains("micromessenger")) {
            return "wechat";
        }
        if (lower.contains("android") || lower.contains("iphone") || lower.contains("mobile")) {
            return "h5";
        }
        return "pc";
    }

    private HttpServletRequest getRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs != null ? attrs.getRequest() : null;
    }
}
