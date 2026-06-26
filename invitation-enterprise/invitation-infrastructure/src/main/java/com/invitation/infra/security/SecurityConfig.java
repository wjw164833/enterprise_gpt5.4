package com.invitation.infra.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security配置
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private AuthenticationEntryPointImpl authenticationEntryPoint;

    @Autowired
    private AccessDeniedHandlerImpl accessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF（使用JWT无状态认证）
            .csrf().disable()
            // 禁用Session
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            // 配置权限
            .authorizeRequests()
                // 公开接口
                .antMatchers("/api/v1/auth/**").permitAll()
                .antMatchers("/api/v1/guest/i/**").permitAll()
                // P1-11: 宾客回复端点需要公开访问
                .antMatchers("/api/v1/guest/reply/**").permitAll()
                .antMatchers("/api/v1/bless/invitations/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/templates/**").permitAll()
                .antMatchers("/api/v1/public/**").permitAll()
                // P0-05: 支付回调端点需要公开访问（微信不会携带JWT）
                .antMatchers("/api/v1/payment/notify/**").permitAll()
                // Swagger文档
                .antMatchers("/doc.html", "/swagger-resources/**", "/webjars/**", "/v2/api-docs").permitAll()
                // P1-04: 移除Actuator公开访问，限制为内网访问
                // Actuator端点不再公开，需要ADMIN角色才能访问
                .antMatchers("/actuator/**").hasRole("ADMIN")
                // WebSocket
                .antMatchers("/ws/**").permitAll()
                // 静态资源
                .antMatchers("/static/**", "/favicon.ico").permitAll()
                // 管理接口需要ADMIN角色
                .antMatchers("/api/v1/admin/**").hasRole("ADMIN")
                // 微信小程序access-token端点需要ADMIN角色（P1-09）
                .antMatchers("/api/v1/wx/mini/access-token").hasRole("ADMIN")
                // 其他接口需要认证
                .anyRequest().authenticated()
            .and()
            // 异常处理
            .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
            .and()
            // 添加JWT过滤器
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
