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
        String token = authService.loginUser(loginRequest);
        JwtAuthResponse jwtAuthResponse = new JwtAuthResponse(token);
        return ResponseEntity.ok(jwtAuthResponse);
    }
}