package com.example.bankcards.dto;

public class UpdateUserRequest {
    private String username;
    private String newPassword;
    private String newRole;

    public UpdateUserRequest() {}

    public UpdateUserRequest(String newRole, String newPassword, String username) {
        this.newRole = newRole;
        this.newPassword = newPassword;
        this.username = username;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    public String getNewRole() { return newRole; }
    public void setNewRole(String newRole) { this.newRole = newRole; }
}