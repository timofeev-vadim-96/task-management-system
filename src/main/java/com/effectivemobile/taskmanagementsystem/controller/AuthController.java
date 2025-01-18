package com.effectivemobile.taskmanagementsystem.controller;

import com.effectivemobile.taskmanagementsystem.controller.dto.JwtAuthenticationResponse;
import com.effectivemobile.taskmanagementsystem.controller.dto.SignInRequest;
import com.effectivemobile.taskmanagementsystem.controller.dto.SignUpRequest;
import com.effectivemobile.taskmanagementsystem.security.AuthService;
import com.effectivemobile.taskmanagementsystem.util.AppRole;
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
        if (request.getRole().equals(AppRole.ROLE_ADMIN) && (userDetails == null ||
                !userDetails.getAuthorities().contains(new SimpleGrantedAuthority(AppRole.ROLE_ADMIN.name())))) {
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
}
