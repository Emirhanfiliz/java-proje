package com.sporttracker.shared.exception;

import com.sporttracker.shared.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Controller katmanında fırlatılan TÜM hataları yakalayan (Catch) Merkezi Yakalayıcı (Interceptor).
 * Projeyi çökmekten kurtarır, kullanıcıya daima JSON ApiResponse döner.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Kendi tanımladığımız bilindik iş kuralları (Mesela "Kullanıcı Bulunamadı")
    @ExceptionHandler(CustomBusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleCustomBusinessException(CustomBusinessException ex) {
        log.error("Business Exception Occurred: {}", ex.getMessage());
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage(), ex.getHttpStatus().value());
        return new ResponseEntity<>(response, ex.getHttpStatus());
    }

    // Beklenmedik "NullPointer, IndexOutOfBounds" vb Tüm Diğer Hatalar - (500 Internal Server Error)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
        log.error("Unhandled Exception Occurred!", ex);
        ApiResponse<Object> response = ApiResponse.error("An unexpected error occurred: " + ex.getMessage(), 500);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
