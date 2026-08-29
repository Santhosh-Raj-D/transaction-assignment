package com.example.transactionstarter.transaction.dto;

import com.example.transactionstarter.transaction.domain.CurrencyCode;
import com.example.transactionstarter.transaction.domain.Transaction;
import com.example.transactionstarter.transaction.domain.TransactionStatus;
import com.example.transactionstarter.transaction.domain.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * TransactionResponse is the object we send back to the client 
 * after the transaction has been processed or retrieved.
 */
public class TransactionResponse {

    private final String id;
    private final String customerId;
    private final BigDecimal amount;
    private final CurrencyCode currency;
    private final TransactionType type;
    private final TransactionStatus status;
    private final Instant createdAt;
    private final Instant updatedAt;

    public TransactionResponse(String id, String customerId, BigDecimal amount, CurrencyCode currency,
                                TransactionType type, TransactionStatus status, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.customerId = customerId;
        this.amount = amount;
        this.currency = currency;
        this.type = type;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getCustomerId(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getType(),
                transaction.getStatus(),
                transaction.getCreatedAt(),
                transaction.getUpdatedAt()
        );
    }

    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public CurrencyCode getCurrency() {
        return currency;
    }

    public TransactionType getType() {
        return type;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
