package com.sporttracker.shared.exception;

import org.springframework.http.HttpStatus;

public class CustomBusinessException extends RuntimeException {

    private final HttpStatus httpStatus;

    public CustomBusinessException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
