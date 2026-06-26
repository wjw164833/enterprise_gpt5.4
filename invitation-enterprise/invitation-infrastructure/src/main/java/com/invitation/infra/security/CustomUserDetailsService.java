package com.invitation.infra.security;

import com.invitation.common.enums.ResultCode;
import com.invitation.common.exception.BusinessException;
import com.invitation.common.model.LoginUser;
import com.invitation.model.entity.User;
import com.invitation.model.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义用户详情服务 - Spring Security集成
 */
@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // username实际上是userId（JWT认证场景）
        Long userId;
        try {
            userId = Long.parseLong(username);
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + userId);
        }
        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        // 构建权限列表
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if (user.getUserType() != null) {
            switch (user.getUserType()) {
                case 3:
                    authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                case 2:
                    authorities.add(new SimpleGrantedAuthority("ROLE_ENTERPRISE"));
                default:
                    authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            }
        }

        return new org.springframework.security.core.userdetails.User(
                String.valueOf(user.getId()), "", authorities);
    }
}
