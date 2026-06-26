package com.invitation.common.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类 - 令牌生成、解析、校验
 */
@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成AccessToken
     */
    public String generateAccessToken(Long userId, String nickname, Integer userType) {
        Map<String, Object> claims = new HashMap<>(4);
        claims.put("userId", userId);
        claims.put("nickname", nickname);
        claims.put("userType", userType);
        claims.put("type", "access");
        return buildToken(claims, accessTokenExpiration);
    }

    /**
     * 生成RefreshToken
     * P0-11: 加入userType claim，确保刷新时能恢复用户角色
     */
    public String generateRefreshToken(Long userId, Integer userType) {
        Map<String, Object> claims = new HashMap<>(4);
        claims.put("userId", userId);
        claims.put("userType", userType);
        claims.put("type", "refresh");
        return buildToken(claims, refreshTokenExpiration);
    }

    /**
     * 构建Token
     */
    private String buildToken(Map<String, Object> claims, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 从Token中获取Claims
     */
    public Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.warn("Token已过期: {}", e.getMessage());
            throw new com.invitation.common.exception.AuthException(
                    com.invitation.common.enums.ResultCode.TOKEN_EXPIRED);
        } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e) {
            log.warn("Token无效: {}", e.getMessage());
            throw new com.invitation.common.exception.AuthException(
                    com.invitation.common.enums.ResultCode.TOKEN_INVALID);
        }
    }

    /**
     * 从Token中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        Object userId = claims.get("userId");
        if (userId instanceof Integer) {
            return ((Integer) userId).longValue();
        }
        return (Long) userId;
    }

    /**
     * 从Token中获取用户类型
     */
    public Integer getUserTypeFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        Object userType = claims.get("userType");
        if (userType instanceof Integer) {
            return (Integer) userType;
        }
        return null;
    }

    /**
     * 校验Token是否有效
     */
    public boolean validateToken(String token) {
        try {
            getClaimsFromToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断是否为RefreshToken
     */
    public boolean isRefreshToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return "refresh".equals(claims.get("type"));
    }

    /**
     * 获取AccessToken过期时间(毫秒)
     */
    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    /**
     * 获取RefreshToken过期时间(毫秒)
     */
    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }
}
