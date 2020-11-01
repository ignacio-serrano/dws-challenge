package com.db.awmd.challenge.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY, reason = "Insufficient funds.")
public class OverdraftException extends RuntimeException {
    public OverdraftException(String message) {
        super(message);
    }
}
