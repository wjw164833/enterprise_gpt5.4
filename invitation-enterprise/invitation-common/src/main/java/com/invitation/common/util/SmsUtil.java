package com.invitation.common.util;

import com.invitation.common.constant.BusinessConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Random;

/**
 * 鐭俊鍙戦€佸伐鍏风被
 */
@Slf4j
@Component
public class SmsUtil {

    @Value("${sms.provider:aliyun}")
    private String provider;

    /**
     * 鐢熸垚6浣嶆暟瀛楅獙璇佺爜
     */
    public String generateCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < BusinessConstant.SMS_CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    /**
     * 鍙戦€佺煭淇￠獙璇佺爜锛堝鎺ョ煭淇″钩鍙帮級
     */
    public boolean sendSmsCode(String phone, String code, String bizType) {
        try {
            // 瀹為檯瀵规帴鐭俊骞冲彴锛堥樋閲屼簯/鑵捐浜戠煭淇℃湇鍔★級
            log.info("鍙戦€佺煭淇￠獙璇佺爜: phone={}, code={}, bizType={}, provider={}", phone, code, bizType, provider);
            // TODO: 瀵规帴闃块噷浜?鑵捐浜戠煭淇PI
            return true;
        } catch (Exception e) {
            log.error("鍙戦€佺煭淇￠獙璇佺爜澶辫触: phone={}, error={}", phone, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 鍙戦€侀€氱煡鐭俊
     */
    public boolean sendNotifySms(String phone, String templateCode, Map<String, String> params) {
        try {
            log.info("鍙戦€侀€氱煡鐭俊: phone={}, template={}", phone, templateCode);
            // TODO: 瀵规帴鐭俊妯℃澘API
            return true;
        } catch (Exception e) {
            log.error("鍙戦€侀€氱煡鐭俊澶辫触: phone={}, error={}", phone, e.getMessage(), e);
            return false;
        }
    }
}

