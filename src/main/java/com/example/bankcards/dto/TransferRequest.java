package com.example.bankcards.dto;

import java.math.BigDecimal;

public class TransferRequest {

    private String sourceCardNumber;
    private String destinationCardNumber;
    private BigDecimal amount;

    public TransferRequest() {}

    public TransferRequest(String sourceCardNumber, String destinationCardNumber, BigDecimal amount) {
        this.sourceCardNumber = sourceCardNumber;
        this.destinationCardNumber = destinationCardNumber;
        this.amount = amount;
    }

    public String getSourceCardNumber() { return sourceCardNumber; }
    public void setSourceCardNumber(String sourceCardNumber) { this.sourceCardNumber = sourceCardNumber; }

    public String getDestinationCardNumber() { return destinationCardNumber; }
    public void setDestinationCardNumber(String destinationCardNumber) { this.destinationCardNumber = destinationCardNumber; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}