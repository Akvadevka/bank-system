package com.example.bankcards.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreateCardRequest {
    private String cardNumber;
    private LocalDate expiryDate;
    private BigDecimal balance;

    public CreateCardRequest() {}

    public CreateCardRequest(String cardNumber, LocalDate expiryDate, BigDecimal balance) {
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.balance = balance;
    }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
}