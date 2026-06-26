package com.invitation.service.user;

import com.invitation.model.entity.User;

public interface UserService {
    User getById(Long id);
    User getByPhone(String phone);
    void updateProfile(User user);
}
