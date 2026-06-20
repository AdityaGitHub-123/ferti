package com.fertilizer.ruleengine.dto;

public class AuthResponse {
    private Long userId;
    private String username;
    private String email;
    private String phone;
    private String token;
    private String message;

    public AuthResponse(Long userId, String username, String email, String phone, String token, String message) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.token = token;
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getToken() {
        return token;
    }

    public String getMessage() {
        return message;
    }
}
