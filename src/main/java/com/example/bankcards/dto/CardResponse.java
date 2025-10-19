package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;
import java.time.LocalDate;
import java.math.BigDecimal;

public class CardResponse {

    private Long id;
    private String maskedCardNumber;
    private LocalDate expiryDate;
    private CardStatus status;
    private BigDecimal balance;

    public CardResponse() {}

    public CardResponse(Long id, String maskedCardNumber, LocalDate expiryDate, CardStatus status, BigDecimal balance) {
        this.id = id;
        this.maskedCardNumber = maskedCardNumber;
        this.expiryDate = expiryDate;
        this.status = status;
        this.balance = balance;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMaskedCardNumber() { return maskedCardNumber; }
    public void setMaskedCardNumber(String maskedCardNumber) { this.maskedCardNumber = maskedCardNumber; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public CardStatus getStatus() { return status; }
    public void setStatus(CardStatus status) { this.status = status; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
}