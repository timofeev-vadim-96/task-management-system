package com.effectivemobile.taskmanagementsystem.service;

import com.effectivemobile.taskmanagementsystem.dao.UserDao;
import com.effectivemobile.taskmanagementsystem.exception.EmailAlreadyExistsException;
import com.effectivemobile.taskmanagementsystem.exception.EntityNotFoundException;
import com.effectivemobile.taskmanagementsystem.exception.UserNotFoundException;
import com.effectivemobile.taskmanagementsystem.model.AppUser;
import com.effectivemobile.taskmanagementsystem.util.AppRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Сервис для работы с пользователями")
@DataJpaTest
@Import({UserServiceImpl.class})
@Transactional(propagation = Propagation.NEVER)
class UserServiceImplTest {
    @Autowired
    private UserServiceImpl userService;

    @SpyBean
    private UserDao userDao;

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void create() {
        AppUser newUser = AppUser.builder()
                .email("newUserEmail@gmail.com")
                .password("password")
                .role(AppRole.ROLE_USER)
                .build();

        AppUser user = userService.create(newUser);

        assertThat(user).isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(newUser);
        verify(userDao, times(1)).existsByEmail(newUser.getEmail());
        verify(userDao, times(1)).save(newUser);
    }

    @Test
    void createNegative() {
        String alreadyExistsEmail = "testUser@gmail.com";
        AppUser newUser = AppUser.builder()
                .email(alreadyExistsEmail)
                .password("password")
                .role(AppRole.ROLE_USER)
                .build();

        assertThrowsExactly(EmailAlreadyExistsException.class, () -> userService.create(newUser));
        verify(userDao, times(1)).existsByEmail(alreadyExistsEmail);
    }

    @Test
    void getUserByEmail() {
        String expectedEmail = "testUser@gmail.com";

        AppUser user = userService.getUserByEmail(expectedEmail);

        assertThat(user).isNotNull().hasFieldOrPropertyWithValue("email", expectedEmail);
        verify(userDao, times(1)).findByEmail(expectedEmail);
    }

    @Test
    void getUserByEmailNegative() {
        String notExistingEmail = "notExist@gmail.com";

        assertThrowsExactly(EntityNotFoundException.class, () -> userService.getUserByEmail(notExistingEmail));
        verify(userDao, times(1)).findByEmail(notExistingEmail);
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    void getById(long id) {
        AppUser user = userService.getById(id);

        assertThat(user).isNotNull().hasFieldOrPropertyWithValue("id", id);
        verify(userDao, times(1)).findById(id);
    }

    @Test
    void getByIdNegative() {
        long notExistingId = 11L;

        assertThrowsExactly(EntityNotFoundException.class, () -> userService.getById(notExistingId));
        verify(userDao, times(1)).findById(notExistingId);
    }

    @Test
    @WithUserDetails(value = "testUser@gmail.com")
    void getCurrentAppUser() {
        String expectedEmail = "testUser@gmail.com";

        AppUser user = userService.getCurrentAppUser();

        assertEquals(expectedEmail, user.getEmail());
    }

    @Test
    void loadUserByUsername() {
        String expectedUsername = "testUser@gmail.com";

        UserDetails userDetails = userService.loadUserByUsername(expectedUsername);

        assertNotNull(userDetails);
        assertEquals(expectedUsername, userDetails.getUsername());
        verify(userDao, times(1)).findByEmail(expectedUsername);
    }

    @Test
    void loadUserByUsernameNegative() {
        String notExistingEmail = "notExist@gmail.com";

        assertThrowsExactly(UserNotFoundException.class, () -> userService.loadUserByUsername(notExistingEmail));
        verify(userDao, times(1)).findByEmail(notExistingEmail);
    }
}