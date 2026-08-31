package com.example.transactionstarter.clientflow.dto;

import com.example.transactionstarter.transaction.domain.CurrencyCode;
import com.example.transactionstarter.transaction.domain.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * What a client sends to pay someone without manually supplying a
 * customerId or transactionId. The client only describes WHO they are
 * (name/flat/contact) and WHAT the payment is - both IDs are generated
 * by the backend.
 */
public class InitiatePaymentRequest {

    @NotBlank(message = "payerName is required")
    private String payerName;

    @NotBlank(message = "payerFlatId is required")
    private String payerFlatId;

    @NotBlank(message = "payerContact is required")
    private String payerContact;

    @NotBlank(message = "receiverId is required")
    private String receiverId;

    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.01", message = "amount must be greater than zero")
    private BigDecimal amount;

    @NotNull(message = "currency is required")
    private CurrencyCode currency;

    @NotNull(message = "type is required")
    private TransactionType type;

    public InitiatePaymentRequest() {
    }

    public InitiatePaymentRequest(String payerName, String payerFlatId, String payerContact, String receiverId,
                                   BigDecimal amount, CurrencyCode currency, TransactionType type) {
        this.payerName = payerName;
        this.payerFlatId = payerFlatId;
        this.payerContact = payerContact;
        this.receiverId = receiverId;
        this.amount = amount;
        this.currency = currency;
        this.type = type;
    }

    public String getPayerName() {
        return payerName;
    }

    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }

    public String getPayerFlatId() {
        return payerFlatId;
    }

    public void setPayerFlatId(String payerFlatId) {
        this.payerFlatId = payerFlatId;
    }

    public String getPayerContact() {
        return payerContact;
    }

    public void setPayerContact(String payerContact) {
        this.payerContact = payerContact;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
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
