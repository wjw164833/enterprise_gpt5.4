package com.invitation.service.user;

import com.invitation.common.util.AesUtil;
import com.invitation.model.entity.User;
import com.invitation.model.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AesUtil aesUtil;

    @Override
    public User getById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public User getByPhone(String phone) {
        String encrypted = aesUtil.encrypt(phone);
        return userMapper.selectByPhone(encrypted);
    }

    @Override
    public void updateProfile(User user) {
        userMapper.updateById(user);
    }
}
