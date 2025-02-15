package com.effectivemobile.taskmanagementsystem.security.listener;

import com.effectivemobile.taskmanagementsystem.security.event.EventAuthenticationSuccessEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationResultListener {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationResultListener.class);

    /**
     * Если попытка аутентификации неудачна - увеличить счетчик попыток
     */
    @EventListener(AuthenticationFailureBadCredentialsEvent.class)
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        String email = extractUsername(event.getAuthentication());
        if (email != null) {
            logger.warn("Failed authentication attempt by email = {}", email);
        }
    }

    /**
     * Если пользователь аутентифицировался в рамках допускаемого количества попыток, то сбросить счетчик
     */
    @EventListener(EventAuthenticationSuccessEvent.class)
    public void onApplicationEvent(EventAuthenticationSuccessEvent event) {
        String email = event.getEmail();
        if (email != null) {
            logger.info("Successful authentication by email = {}", email);
        }
    }

    private String extractUsername(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        return principal instanceof String ? (String) principal : null;
    }
}
