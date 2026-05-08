package com.sporttracker.shared.dto;

public class UserDto {
    private String id;
    private String username;
    private String email;
    private String role;

    public UserDto() {}

    public UserDto(String id, String username, String email, String role) {
        this.id = id; this.username = username; this.email = email; this.role = role;
    }

    public String getId()       { return id; }
    public String getUsername() { return username; }
    public String getEmail()    { return email; }
    public String getRole()     { return role; }

    public void setId(String id)           { this.id = id; }
    public void setUsername(String u)      { this.username = u; }
    public void setEmail(String email)     { this.email = email; }
    public void setRole(String role)       { this.role = role; }
}
