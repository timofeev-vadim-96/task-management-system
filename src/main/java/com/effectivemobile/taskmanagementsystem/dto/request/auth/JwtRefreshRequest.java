package com.effectivemobile.taskmanagementsystem.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Запрос на обновление токенов по refresh token")
public class JwtRefreshRequest {
    @NotBlank(message = "Jwt must not be empty or null")
    @Schema(description = "refresh-токен")
    private String refreshToken;
}
