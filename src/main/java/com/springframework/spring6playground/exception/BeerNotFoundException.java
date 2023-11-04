package com.springframework.spring6playground.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Beer Not Found")
public class BeerNotFoundException extends RuntimeException{
    public BeerNotFoundException() {
    }

    public BeerNotFoundException(String message) {
        super(message);
    }

    public BeerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeerNotFoundException(Throwable cause) {
        super(cause);
    }

    public BeerNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
