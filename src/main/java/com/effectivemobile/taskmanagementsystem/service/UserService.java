package com.effectivemobile.taskmanagementsystem.service;

import com.effectivemobile.taskmanagementsystem.model.AppUser;

public interface UserService {
    AppUser create(AppUser user);

    AppUser getUserByEmail(String email);

    AppUser getById(long id);

    AppUser getCurrentAppUser();
}
