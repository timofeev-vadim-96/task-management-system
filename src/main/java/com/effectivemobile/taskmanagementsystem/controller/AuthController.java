package com.effectivemobile.taskmanagementsystem.controller;

import com.effectivemobile.taskmanagementsystem.dto.request.auth.JwtAuthenticationResponse;
import com.effectivemobile.taskmanagementsystem.dto.request.auth.JwtRefreshRequest;
import com.effectivemobile.taskmanagementsystem.dto.request.auth.SignInRequest;
import com.effectivemobile.taskmanagementsystem.dto.request.auth.SignUpRequest;
import com.effectivemobile.taskmanagementsystem.security.AuthService;
import com.effectivemobile.taskmanagementsystem.util.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Контроллер аутентификации", description = "Контроллер для аутентификации и регистрации пользователей")
public class AuthController {
    private final AuthService authenticationService;

    @PostMapping("/api/v1/sign-up")
    @Operation(summary = "Регистрация пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "пользователь зарегистрирован"),
            @ApiResponse(responseCode = "403",
                    description = "ошибка при попытке создать администратора без прав администратора"),
            @ApiResponse(responseCode = "409", description = "пользователь с таким email уже существует")
    })
    public ResponseEntity<JwtAuthenticationResponse> signUp(@AuthenticationPrincipal UserDetails userDetails,
                                                            @RequestBody @Valid SignUpRequest request) {
        //нового Админа может зарегистрировать только авторизованный АДМИН
        if (request.getRole().equals(Role.ROLE_ADMIN) && (userDetails == null ||
                !userDetails.getAuthorities().contains(new SimpleGrantedAuthority(Role.ROLE_ADMIN.name())))) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        var token = authenticationService.signUp(request);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    @PostMapping("/api/v1/sign-in")
    @Operation(summary = "Авторизация пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "пользователь зарегистрирован"),
            @ApiResponse(responseCode = "403", description = "email или пароль введены не верно")
    })
    public ResponseEntity<JwtAuthenticationResponse> signIn(@RequestBody @Valid SignInRequest request) {
        var token = authenticationService.signIn(request);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    @PostMapping("/api/v1/token/refresh")
    @Operation(summary = "Обновление токенов по refresh токену")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "токены обновлены"),
            @ApiResponse(responseCode = "403", description = "refresh токен не действителен или не валиден")
    })
    public ResponseEntity<JwtAuthenticationResponse> refreshToken(@RequestBody @Valid JwtRefreshRequest refreshToken) {
        var tokens = authenticationService.refreshToken(refreshToken.getRefreshToken());
        return new ResponseEntity<>(tokens, HttpStatus.OK);
    }
}
