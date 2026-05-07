package com.sporttracker.shared.exception;

import com.sporttracker.shared.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        ApiResponse<Object> response = ApiResponse.error(message, HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomBusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleCustomBusinessException(CustomBusinessException ex) {
        log.error("Business Exception Occurred: {}", ex.getMessage());
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage(), ex.getHttpStatus().value());
        return new ResponseEntity<>(response, ex.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
        log.error("Unhandled Exception Occurred!", ex);
        ApiResponse<Object> response = ApiResponse.error("An unexpected error occurred: " + ex.getMessage(), 500);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
