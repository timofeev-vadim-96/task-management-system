package com.effectivemobile.taskmanagementsystem.service;

import com.effectivemobile.taskmanagementsystem.dao.UserDao;
import com.effectivemobile.taskmanagementsystem.exception.EmailAlreadyExistsException;
import com.effectivemobile.taskmanagementsystem.exception.EntityNotFoundException;
import com.effectivemobile.taskmanagementsystem.exception.UserNotFoundException;
import com.effectivemobile.taskmanagementsystem.model.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserDao userDao;

    /**
     * Создание пользователя
     *
     * @return созданный пользователь
     */
    @Transactional
    public AppUser create(AppUser user) {
        if (userDao.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException("Пользователь с таким email уже существует");
        }

        return userDao.save(user);
    }

    /**
     * Получение пользователя по имени пользователя
     *
     * @return пользователь
     */
    @Transactional(readOnly = true)
    public AppUser getUserByEmail(String email) {
        return userDao.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User with email = %s is not found".formatted(email)));
    }

    @Override
    @Transactional(readOnly = true)
    public AppUser getById(long id) {
        return userDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id = %d is not found".formatted(id)));
    }

    /**
     * Получение текущего пользователя
     *
     * @return текущий пользователь
     */
    @Transactional(readOnly = true)
    public AppUser getCurrentAppUser() {
        // Получение имени пользователя из контекста Spring Security
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        return getUserByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = userDao.findByEmail(username)
                .orElseThrow(() ->
                        new UserNotFoundException("User with login: %s not found".formatted(username)));

        return new User(
                user.getEmail(),
                user.getPassword(),
                Set.of(new SimpleGrantedAuthority(user.getRole().toString())));
    }
}
