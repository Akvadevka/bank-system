package com.example.bankcards.controller;

import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.AdminCreateCardRequest;
import com.example.bankcards.service.CardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.bankcards.dto.UserResponse;
import com.example.bankcards.service.AdminService;
import com.example.bankcards.dto.UserManagementRequest;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final CardService cardService;
    private final AdminService adminService;

    public AdminController(CardService cardService, AdminService adminService) {
        this.cardService = cardService;
        this.adminService = adminService;
    }

    @PostMapping("/cards")
    public ResponseEntity<CardResponse> createCard(@RequestBody AdminCreateCardRequest request) {
        try {
            CardResponse response = cardService.createCardForUser(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/cards")
    public ResponseEntity<Page<CardResponse>> getAllCards(Pageable pageable) {
        Page<CardResponse> cardsPage = cardService.getAllCards(pageable);
        return ResponseEntity.ok(cardsPage);
    }

    @PutMapping("/cards/{cardId}/status/{status}")
    public ResponseEntity<CardResponse> setCardStatus(
            @PathVariable Long cardId,
            @PathVariable com.example.bankcards.entity.CardStatus status) {
        try {
            CardResponse response = cardService.adminUpdateCardStatus(cardId, status);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/cards/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long cardId) {
        try {
            cardService.deleteCardById(cardId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponse> createUser(@RequestBody UserManagementRequest request) {
        try {
            UserResponse newUser = adminService.createUser(request);
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long userId, @RequestBody UserManagementRequest request) {
        try {
            UserResponse updatedUser = adminService.updateUser(userId, request);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/users")
    public ResponseEntity<Page<UserResponse>> getAllUsers(Pageable pageable) {
        Page<UserResponse> usersPage = adminService.getAllUsers(pageable);
        return ResponseEntity.ok(usersPage);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        try {
            adminService.deleteUser(userId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}