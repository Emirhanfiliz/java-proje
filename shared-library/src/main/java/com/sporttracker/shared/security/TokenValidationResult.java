package com.sporttracker.shared.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class TokenValidationResult {

    private final boolean valid;
    private final String errorCode;
    private final String errorMessage;

    public static TokenValidationResult valid() {
        return TokenValidationResult.builder()
                .valid(true)
                .errorCode(null)
                .errorMessage(null)
                .build();
    }

    public static TokenValidationResult invalid(String errorCode, String errorMessage) {
        return TokenValidationResult.builder()
                .valid(false)
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .build();
    }
}
