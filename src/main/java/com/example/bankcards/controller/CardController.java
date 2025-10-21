package com.example.bankcards.controller;

import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.service.CardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.bankcards.dto.TransferRequest;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping
    public ResponseEntity<CardResponse> createCard(@RequestBody CreateCardRequest request) {
        try {
            CardResponse response = cardService.createCard(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<CardResponse>> getAllCards() {
        List<CardResponse> cards = cardService.getAllCardsForCurrentUser();
        return ResponseEntity.ok(cards);
    }

    @PutMapping("/{cardId}/block")
    public ResponseEntity<CardResponse> blockCard(@PathVariable Long cardId) {
        try {
            CardResponse response = cardService.updateCardStatus(cardId, com.example.bankcards.entity.CardStatus.BLOCKED);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/{cardId}/unblock")
    public ResponseEntity<CardResponse> unblockCard(@PathVariable Long cardId) {
        try {
            CardResponse response = cardService.updateCardStatus(cardId, com.example.bankcards.entity.CardStatus.ACTIVE);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody TransferRequest request) {
        try {
            String result = cardService.transferFunds(request);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Transaction failed due to system error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}