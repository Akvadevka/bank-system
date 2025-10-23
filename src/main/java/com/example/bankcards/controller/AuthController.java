package com.example.bankcards.controller;

import com.example.bankcards.dto.JwtAuthResponse;
import com.example.bankcards.dto.LoginRequest;
import com.example.bankcards.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.bankcards.dto.RefreshRequest;

import org.springframework.security.authentication.BadCredentialsException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody LoginRequest registerRequest) {
        try {
            String response = authService.registerUser(registerRequest);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@RequestBody LoginRequest loginRequest) {
        System.out.println("Login attempt: " + loginRequest.getUsername());
        try {
            JwtAuthResponse response = authService.loginUser(loginRequest);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            System.err.println("LOGIN ERROR: BadCredentialsException - " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            System.err.println("LOGIN ERROR: Other Exception - " + e.getClass().getName() + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthResponse> refresh(@RequestBody RefreshRequest refreshRequest) {
        String refreshToken = refreshRequest.getRefreshToken();

        if (refreshToken == null || refreshToken.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            JwtAuthResponse response = authService.refreshAccessToken(refreshToken);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            System.err.println("Refresh Token failed: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}