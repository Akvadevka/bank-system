package com.example.bankcards.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AdminCreateCardRequest {
    private Long userId;
    private String cardNumber;
    private LocalDate expiryDate;
    private BigDecimal balance;

    public AdminCreateCardRequest() {}

    public AdminCreateCardRequest(Long userId, String cardNumber, LocalDate expiryDate, BigDecimal balance) {
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.balance = balance;
        this.userId = userId;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
}