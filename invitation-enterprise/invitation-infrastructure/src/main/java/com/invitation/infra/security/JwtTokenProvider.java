package com.invitation.infra.security;

import com.invitation.common.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * JWT令牌提供者 - 生成和管理JWT令牌
 */
@Slf4j
@Component
public class JwtTokenProvider {

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 生成令牌对（accessToken + refreshToken）
     * P0-11: refreshToken也包含userType
     */
    public Map<String, String> generateTokenPair(Long userId, String nickname, Integer userType) {
        String accessToken = jwtUtil.generateAccessToken(userId, nickname, userType);
        String refreshToken = jwtUtil.generateRefreshToken(userId, userType);

        Map<String, String> tokenMap = new HashMap<>(4);
        tokenMap.put("accessToken", accessToken);
        tokenMap.put("refreshToken", refreshToken);
        tokenMap.put("tokenType", "Bearer");
        tokenMap.put("expiresIn", String.valueOf(jwtUtil.getAccessTokenExpiration() / 1000));
        return tokenMap;
    }

    /**
     * 通过RefreshToken刷新AccessToken
     * P0-11: 从refreshToken中提取userType
     */
    public Map<String, String> refreshAccessToken(String refreshToken) {
        if (!jwtUtil.isRefreshToken(refreshToken)) {
            throw new com.invitation.common.exception.AuthException(
                    com.invitation.common.enums.ResultCode.TOKEN_INVALID);
        }
        Long userId = jwtUtil.getUserIdFromToken(refreshToken);
        Integer userType = jwtUtil.getUserTypeFromToken(refreshToken);
        // 生成新的Token对，userType从refreshToken中恢复
        return generateTokenPair(userId, null, userType != null ? userType : 1);
    }

    /**
     * 验证Token有效性
     */
    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    /**
     * 从Token获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        return jwtUtil.getUserIdFromToken(token);
    }
}
