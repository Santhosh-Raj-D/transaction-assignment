package com.example.transactionstarter.clientflow.dto;

import com.example.transactionstarter.transaction.domain.CurrencyCode;
import com.example.transactionstarter.transaction.domain.TransactionStatus;

import java.math.BigDecimal;

/** What the client receives back: both backend-generated IDs plus the resulting transaction state. */
public class InitiatePaymentResponse {

    private final String customerId;
    private final String transactionId;
    private final TransactionStatus status;
    private final BigDecimal amount;
    private final CurrencyCode currency;

    public InitiatePaymentResponse(String customerId, String transactionId, TransactionStatus status,
                                    BigDecimal amount, CurrencyCode currency) {
        this.customerId = customerId;
        this.transactionId = transactionId;
        this.status = status;
        this.amount = amount;
        this.currency = currency;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public CurrencyCode getCurrency() {
        return currency;
    }
}
