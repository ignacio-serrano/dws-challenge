package com.db.awmd.challenge.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Account from/to not found.")
public class TransferAccountNotFound extends RuntimeException {
    public TransferAccountNotFound(String message) {
        super(message);
    }
}
