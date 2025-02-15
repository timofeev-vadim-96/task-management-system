package com.effectivemobile.taskmanagementsystem.exception;

import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.security.sasl.AuthenticationException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * Catches exceptions when the uniqueness of fields in the database is violated
     */
    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Void> handleEmailAlreadyExistsException(EmailAlreadyExistsException e) {
        log.warn(e.getMessage());
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    /**
     * Catches exceptions when token is not valid
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({TokenNotValidException.class, JwtException.class})
    public ResponseEntity<Void> handleTokenNotValidException(TokenNotValidException e) {
        log.warn(e.getMessage());
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**
     * Catches errors when the desired or nested entity is not found.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Void> handleEntityNotFoundException(EntityNotFoundException e) {
        log.warn(e.getMessage());
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Catches authentication errors
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Void> handleAuthenticationException(AuthenticationException e) {
        log.warn(e.getMessage());
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Catches errors when users attempt to access other people's entities
     */
    @ExceptionHandler(AttemptingAccessOtherUserEntityException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<Void> handleAttemptingAccessOtherUserEntityException(
            AttemptingAccessOtherUserEntityException e) {
        log.warn(e.getMessage());
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    /**
     * Catch exceptions when trying to authenticate with a non-existent email
     */
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<Void> handleUserNotFoundException(UserNotFoundException e) {
        log.warn(e.getMessage());
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
}
