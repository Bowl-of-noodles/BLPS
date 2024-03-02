package com.javadevjournal.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class NoAuthorityException extends RuntimeException{
    public NoAuthorityException(String message) {
        super(message);
    }
}
