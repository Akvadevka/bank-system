package com.example.bankcards.dto;

import com.example.bankcards.entity.Role;

import java.time.LocalDateTime;
import java.util.Collection;

public class UserResponse {

    private Long id;
    private String username;
    private Collection<Role> roles;

    public UserResponse(Long id, String username, Collection<Role> roles) {
        this.id = id;
        this.username = username;
        this.roles = roles;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Collection<Role> getRoles() { return roles; }
    public void setRoles(Collection<Role> roles) { this.roles = roles; }

}