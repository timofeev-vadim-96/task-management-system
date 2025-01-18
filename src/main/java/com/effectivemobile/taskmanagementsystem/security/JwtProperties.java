package com.effectivemobile.taskmanagementsystem.security;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties(prefix = "application.security.jwt")
@Getter
public class JwtProperties {
    private final String secretKey;

    private final Long expiration;

    private final Long refreshTokenExpiration;

    @ConstructorBinding
    public JwtProperties(String secretKey, Long expiration, Long refreshTokenExpiration) {
        this.secretKey = secretKey;
        this.expiration = expiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }
}
