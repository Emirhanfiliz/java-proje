package com.sporttracker.shared.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Proje içerisindeki mantıksal veya kuralsal hataların tümünde fırlatılacak
 * Özel (Custom) Exception Sınıfı. İçinde HTTP Durum Kodunu (404 Not Found, 400 Bad Request) da taşır.
 */
@Getter
public class CustomBusinessException extends RuntimeException {

    private final HttpStatus httpStatus;

    public CustomBusinessException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

}
