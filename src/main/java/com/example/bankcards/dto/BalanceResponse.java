package com.example.bankcards.dto;

import java.math.BigDecimal;

public class BalanceResponse {
    private String maskedCardNumber;
    private BigDecimal balance;

    public BalanceResponse(){}
    public BalanceResponse(String maskedCardNumber, BigDecimal balance){
        this.balance = balance;
        this.maskedCardNumber = maskedCardNumber;
    }
    public String getMaskedCardNumber() { return maskedCardNumber; }
    public void setMaskedCardNumber(String maskedCardNumber) { this.maskedCardNumber = maskedCardNumber; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
}