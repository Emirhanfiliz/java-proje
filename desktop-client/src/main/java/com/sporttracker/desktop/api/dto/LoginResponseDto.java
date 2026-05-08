package com.sporttracker.desktop.api.dto;

public class LoginResponseDto {
    private String token;
    private String username;
    private String email;

    public String getToken()    { return token; }
    public String getUsername() { return username; }
    public String getEmail()    { return email; }
}
