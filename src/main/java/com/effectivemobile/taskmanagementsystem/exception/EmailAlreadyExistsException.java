package com.effectivemobile.taskmanagementsystem.exception;

import org.springframework.dao.DataAccessException;

public class EmailAlreadyExistsException extends DataAccessException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
