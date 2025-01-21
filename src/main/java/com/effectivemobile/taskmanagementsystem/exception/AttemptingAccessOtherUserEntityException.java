package com.effectivemobile.taskmanagementsystem.exception;

import org.springframework.dao.DataAccessException;

public class AttemptingAccessOtherUserEntityException extends DataAccessException {
    public AttemptingAccessOtherUserEntityException(String message) {
        super(message);
    }
}
