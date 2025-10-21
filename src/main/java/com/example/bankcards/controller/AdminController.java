package com.example.bankcards.controller;

import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.service.CardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.bankcards.dto.UserResponse;
import com.example.bankcards.service.AdminService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final CardService cardService;
    private final AdminService adminService;

    public AdminController(CardService cardService, AdminService adminService) {
        this.cardService = cardService;
        this.adminService = adminService;
    }

    @GetMapping("/cards")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<CardResponse>> getAllCards(Pageable pageable) {
        Page<CardResponse> cardsPage = cardService.getAllCards(pageable);
        return ResponseEntity.ok(cardsPage);
    }

    @DeleteMapping("/cards/{cardId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCard(@PathVariable Long cardId) {
        try {
            cardService.deleteCardById(cardId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponse>> getAllUsers(Pageable pageable) {
        Page<UserResponse> usersPage = adminService.getAllUsers(pageable);
        return ResponseEntity.ok(usersPage);
    }
}