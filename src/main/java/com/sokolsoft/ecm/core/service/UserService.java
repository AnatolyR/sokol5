package com.sokolsoft.ecm.core.service;

import com.sokolsoft.ecm.core.model.User;

public interface UserService {
    User getUserByLogin(String login);

    User getCurrentUser();
}
