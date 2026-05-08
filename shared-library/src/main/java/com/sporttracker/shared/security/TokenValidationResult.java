package com.sporttracker.shared.security;

public class TokenValidationResult {

    private final boolean valid;
    private final String  errorCode;
    private final String  errorMessage;

    public TokenValidationResult(boolean valid, String errorCode, String errorMessage) {
        this.valid        = valid;
        this.errorCode    = errorCode;
        this.errorMessage = errorMessage;
    }

    public static TokenValidationResult valid() {
        return new TokenValidationResult(true, null, null);
    }

    public static TokenValidationResult invalid(String errorCode, String errorMessage) {
        return new TokenValidationResult(false, errorCode, errorMessage);
    }

    public boolean isValid()         { return valid; }
    public String  getErrorCode()    { return errorCode; }
    public String  getErrorMessage() { return errorMessage; }

    @Override
    public String toString() {
        return "TokenValidationResult{valid=" + valid + ", errorCode='" + errorCode + "', errorMessage='" + errorMessage + "'}";
    }
}
