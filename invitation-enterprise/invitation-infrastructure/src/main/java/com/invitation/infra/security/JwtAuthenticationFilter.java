package com.invitation.infra.security;

import com.invitation.common.constant.ApiConstant;
import com.invitation.common.model.LoginUser;
import com.invitation.common.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * JWT认证过滤器 - 拦截请求校验Token
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        // 公开接口跳过认证
        if (isPublicApi(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 从Header中获取Token
        String token = resolveToken(request);
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 解析Token
            Long userId = jwtUtil.getUserIdFromToken(token);
            Integer userType = jwtUtil.getUserTypeFromToken(token);

            // 构建LoginUser
            LoginUser loginUser = new LoginUser();
            loginUser.setUserId(userId);
            loginUser.setUserType(userType);
            loginUser.setToken(token);

            // 构建权限列表
            // 角色继承策略：ADMIN > ENTERPRISE > USER
            // userType=1(普通用户): ROLE_USER
            // userType=2(企业用户): ROLE_ENTERPRISE + ROLE_USER
            // userType=3(管理员): ROLE_ADMIN + ROLE_ENTERPRISE + ROLE_USER
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            if (userType != null) {
                switch (userType) {
                    case 3:
                        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                        // 有意 fall through: 管理员同时拥有企业用户和普通用户权限
                    case 2:
                        authorities.add(new SimpleGrantedAuthority("ROLE_ENTERPRISE"));
                        // 有意 fall through: 企业用户同时拥有普通用户权限
                    default:
                        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                }
            }

            // 设置安全上下文
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(loginUser, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 设置LoginUser到ThreadLocal
            LoginUser.set(loginUser);
        } catch (Exception e) {
            log.warn("JWT认证失败: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            LoginUser.remove();
        }
    }

    /**
     * 从请求头解析Token
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 判断是否为公开API
     */
    private boolean isPublicApi(String uri) {
        return uri.startsWith(ApiConstant.PUBLIC)
                || uri.startsWith("/api/v1/guest/i/")
                || uri.startsWith("/api/v1/guest/reply/")
                || uri.startsWith("/api/v1/auth/")
                || uri.startsWith("/api/v1/payment/notify/")
                || uri.startsWith("/api/v1/bless/invitations/")
                || uri.startsWith("/api/v1/templates")
                || uri.startsWith("/doc.html")
                || uri.startsWith("/swagger-resources")
                || uri.startsWith("/webjars/")
                || uri.startsWith("/v2/api-docs")
                || uri.startsWith("/ws/")
                || uri.equals("/favicon.ico");
    }
}
