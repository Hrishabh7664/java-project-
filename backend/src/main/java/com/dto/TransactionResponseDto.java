package com.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponseDto {

    private Long id;
    private String transactionId;
    private String description;
    private BigDecimal amount;
    private String type;
    private String status;
    private LocalDateTime timestamp;
    private String accountNumber;
    private String holderName;

    public TransactionResponseDto() {
    }

    public TransactionResponseDto(Long id, String transactionId, String description, BigDecimal amount, String type, String status, LocalDateTime timestamp, String accountNumber, String holderName) {
        this.id = id;
        this.transactionId = transactionId;
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.status = status;
        this.timestamp = timestamp;
        this.accountNumber = accountNumber;
        this.holderName = holderName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }
}
