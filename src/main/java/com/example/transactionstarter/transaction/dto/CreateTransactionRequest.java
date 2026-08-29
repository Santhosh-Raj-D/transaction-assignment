package com.example.transactionstarter.transaction.dto;

import com.example.transactionstarter.transaction.domain.CurrencyCode;
import com.example.transactionstarter.transaction.domain.TransactionType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * CreateTransactionRequest is the data we allow the client to send
 * when asking the application to create a new transaction.
 */
public class CreateTransactionRequest {

    @NotBlank(message = "transactionId is required")
    private String transactionId;

    @NotBlank(message = "customerId is required")
    private String customerId;

    // ASSUMPTION (no assigned variant available): max amount capped at 1,000,000.
    // Replace with the real limit from your invitation email if it differs.
    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.01", message = "amount must be greater than zero")
    @DecimalMax(value = "1000000.00", message = "amount must not exceed 1,000,000")
    private BigDecimal amount;

    @NotNull(message = "currency is required")
    private CurrencyCode currency;

    @NotNull(message = "type is required")
    private TransactionType type;

    public CreateTransactionRequest() {
    }

    public CreateTransactionRequest(String transactionId, String customerId, BigDecimal amount, CurrencyCode currency, TransactionType type) {
        this.transactionId = transactionId;
        this.customerId = customerId;
        this.amount = amount;
        this.currency = currency;
        this.type = type;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public CurrencyCode getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyCode currency) {
        this.currency = currency;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }
}
