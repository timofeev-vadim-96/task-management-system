package com.effectivemobile.taskmanagementsystem.util;

import com.effectivemobile.taskmanagementsystem.dao.UserDao;
import com.effectivemobile.taskmanagementsystem.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserPasswordCryptographer {
    private static final Pattern BCRYPT_PATTERN =
            Pattern.compile("\\A\\$2(a|y|b)?\\$(\\d\\d)\\$[./0-9A-Za-z]{53}");

    private final UserDao dao;

    private final PasswordEncoder passwordEncoder;

    @EventListener(ContextRefreshedEvent.class)
    @Transactional
    public void encodeUsersPasswords() {
        List<User> users = dao.findAll();

        if (!users.isEmpty()) {
            String password = users.get(0).getPassword();
            if (isAlreadyEncoded(password)) {
                return;
            }
        }

        log.info("encoding passwords...");
        for (User user : users) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        dao.saveAll(users);
    }

    private boolean isAlreadyEncoded(String password) {
        Matcher matcher = BCRYPT_PATTERN.matcher(password);

        boolean matches = matcher.matches();
        log.info("passwords already encoded? {}", matches);
        return matches;
    }
}