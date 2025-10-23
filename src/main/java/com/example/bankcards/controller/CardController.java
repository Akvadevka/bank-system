package com.example.bankcards.controller;

import com.example.bankcards.dto.BalanceResponse;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.AdminCreateCardRequest;
import com.example.bankcards.service.CardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.bankcards.dto.TransferRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<CardResponse> getCardDetails(@PathVariable Long cardId) {
        try {
            CardResponse card = cardService.getCardByIdForCurrentUser(cardId);
            return ResponseEntity.ok(card);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{cardId}/balance")
    public ResponseEntity<BalanceResponse> getCardBalance(@PathVariable Long cardId) {
        try {
            BalanceResponse balance = cardService.getCardBalanceForCurrentUser(cardId);
            return ResponseEntity.ok(balance);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<Page<CardResponse>> getAllCards(@PageableDefault(size = 10) Pageable pageable) {
        Page<CardResponse> cards = cardService.getAllCardsForCurrentUser(pageable);
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