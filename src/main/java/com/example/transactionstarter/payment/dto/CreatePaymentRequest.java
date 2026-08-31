package com.example.transactionstarter.payment.dto;

import com.example.transactionstarter.payment.domain.PaymentMethod;
import com.example.transactionstarter.transaction.domain.CurrencyCode;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * What a client sends to request and process a payment. senderAccountRef
 * and receiverAccountRef must be non-sensitive, demo-safe references
 * (e.g. a VPA, masked card, wallet ID) - never raw card numbers, CVVs,
 * PINs, or passwords.
 */
public class CreatePaymentRequest {

    @NotBlank(message = "payerId is required")
    private String payerId;

    @NotBlank(message = "payeeId is required")
    private String payeeId;

    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.01", message = "amount must be greater than zero")
    @DecimalMax(value = "1000000.00", message = "amount must not exceed 1,000,000")
    private BigDecimal amount;

    @NotNull(message = "currency is required")
    private CurrencyCode currency;

    @NotNull(message = "method is required")
    private PaymentMethod method;

    @NotBlank(message = "senderBankName is required")
    private String senderBankName;

    @NotBlank(message = "senderAccountRef is required")
    private String senderAccountRef;

    @NotBlank(message = "receiverBankName is required")
    private String receiverBankName;

    @NotBlank(message = "receiverAccountRef is required")
    private String receiverAccountRef;

    public CreatePaymentRequest() {
    }

    public String getPayerId() {
        return payerId;
    }

    public void setPayerId(String payerId) {
        this.payerId = payerId;
    }

    public String getPayeeId() {
        return payeeId;
    }

    public void setPayeeId(String payeeId) {
        this.payeeId = payeeId;
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

    public PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(PaymentMethod method) {
        this.method = method;
    }

    public String getSenderBankName() {
        return senderBankName;
    }

    public void setSenderBankName(String senderBankName) {
        this.senderBankName = senderBankName;
    }

    public String getSenderAccountRef() {
        return senderAccountRef;
    }

    public void setSenderAccountRef(String senderAccountRef) {
        this.senderAccountRef = senderAccountRef;
    }

    public String getReceiverBankName() {
        return receiverBankName;
    }

    public void setReceiverBankName(String receiverBankName) {
        this.receiverBankName = receiverBankName;
    }

    public String getReceiverAccountRef() {
        return receiverAccountRef;
    }

    public void setReceiverAccountRef(String receiverAccountRef) {
        this.receiverAccountRef = receiverAccountRef;
    }
}
