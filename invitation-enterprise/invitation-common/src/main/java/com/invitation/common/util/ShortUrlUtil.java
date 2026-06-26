package com.invitation.common.util;

import com.invitation.common.constant.BusinessConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Random;

/**
 * 短链生成工具
 */
@Slf4j
@Component
public class ShortUrlUtil {

    private static final String CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final Random RANDOM = new SecureRandom();

    /**
     * 生成短链码
     */
    public String generateShortCode() {
        return generateShortCode(BusinessConstant.SHORT_CODE_LENGTH);
    }

    /**
     * 生成指定长度的短链码
     */
    public String generateShortCode(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    /**
     * 根据ID生成短链码（Base62编码）
     */
    public String encodeId(Long id) {
        if (id == null || id <= 0) {
            return generateShortCode();
        }
        StringBuilder sb = new StringBuilder();
        long num = id;
        while (num > 0) {
            int remainder = (int) (num % 62);
            sb.append(CHARACTERS.charAt(remainder));
            num = num / 62;
        }
        return sb.reverse().toString();
    }

    /**
     * 解码短链码为ID
     */
    public Long decodeToId(String shortCode) {
        long id = 0;
        for (int i = 0; i < shortCode.length(); i++) {
            char c = shortCode.charAt(i);
            int index = CHARACTERS.indexOf(c);
            if (index < 0) {
                return null;
            }
            id = id * 62 + index;
        }
        return id;
    }
}
