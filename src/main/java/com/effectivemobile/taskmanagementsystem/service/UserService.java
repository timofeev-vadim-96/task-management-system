package com.effectivemobile.taskmanagementsystem.service;

import com.effectivemobile.taskmanagementsystem.model.User;

public interface UserService {
    User create(User user);

    User getUserByEmail(String email);

    User getById(long id);

    User getCurrentAppUser();
}
