package com.examcontrol.dto;

public class LoginResponse {
    private String token;
    private String username;
    private String fullName;
    private String role;
    private Integer userId;

    public LoginResponse(String token, String username, String fullName, String role, Integer userId) {
        this.token = token;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
        this.userId = userId;
    }

    public String getToken() { return token; }
    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }
    public Integer getUserId() { return userId; }
}
