package com.effectivemobile.taskmanagementsystem.controller;

import com.effectivemobile.taskmanagementsystem.controller.dto.SignInRequest;
import com.effectivemobile.taskmanagementsystem.controller.dto.SignUpRequest;
import com.effectivemobile.taskmanagementsystem.exception.UserNotFoundException;
import com.effectivemobile.taskmanagementsystem.model.AppUser;
import com.effectivemobile.taskmanagementsystem.service.UserServiceImpl;
import com.effectivemobile.taskmanagementsystem.util.AppRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Тест безопасности контроллера аутентификации")
class AuthControllerSecurityTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserServiceImpl userService;

    @Autowired
    private PasswordEncoder encoder;

    @BeforeEach
    void stubbing() {
        when(userService.create(any(AppUser.class))).thenReturn(AppUser.builder().build());
    }

    /**
     * Users with the ADMIN role are allowed to create new admins
     */
    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void signUpToCreateAdminByAdmin() throws Exception {
        SignUpRequest request = new SignUpRequest("newAdmin@gmail.com", "password", AppRole.ROLE_ADMIN);

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("token")));
    }

    @Test
    void signUpNewUser() throws Exception {
        SignUpRequest request = new SignUpRequest("newUser@gmail.com", "password", AppRole.ROLE_USER);

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("token")));
    }

    /**
     * Users with the USER role cannot sign up as admins.
     */
    @Test
    @WithMockUser(authorities = "ROLE_USER")
    void signUpToCreateAdminByUser() throws Exception {
        SignUpRequest request = new SignUpRequest("newAdmin@gmail.com", "password", AppRole.ROLE_ADMIN);

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    /**
     * Unauthorized users cannot sign up as admins.
     */
    @Test
    void signUpToCreateAdminByUnauthorized() throws Exception {
        SignUpRequest request = new SignUpRequest("newAdmin@gmail.com", "password", AppRole.ROLE_ADMIN);

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void signIn() throws Exception {
        final String email = "some@gmail.com";
        AppUser user = AppUser.builder()
                .id(1L)
                .email(email)
                .password(encoder.encode("password"))
                .role(AppRole.ROLE_USER)
                .build();
        when(userService.loadUserByUsername(email)).thenReturn(user);
        when(userService.getUserByEmail(email)).thenReturn(user);
        SignInRequest request = new SignInRequest(email, "password");

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("token")));
    }

    @Test
    void signInDenied() throws Exception {
        final String email = "some@gmail.com";
        when(userService.loadUserByUsername(email))
                .thenThrow(new UserNotFoundException("User not found"));
        SignInRequest request = new SignInRequest(email, "password");

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}