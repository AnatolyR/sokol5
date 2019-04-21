package com.sokolsoft.ecm.core.service;

import com.sokolsoft.ecm.core.model.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public User getUserByLogin(String login) {
        if ("admin".equals(login)) {
            User user = new User();
            user.setTitle("Admin");
            user.setId(UUID.fromString("52cc85b5-fab7-4365-a9cd-94afac1f0e8d"));
            return user;
        }
        throw new RuntimeException("User not found");
    }

    @Override
    public User getCurrentUser() {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        return getUserByLogin(login);
    }
}