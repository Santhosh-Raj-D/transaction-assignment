package com.example.transactionstarter.payment.dto;

import com.example.transactionstarter.payment.domain.PaymentStatus;
import com.example.transactionstarter.payment.domain.PaymentTransaction;

import java.time.Instant;

/** What the client receives back when fetching the payment transaction(s) for a payment order. */
public class PaymentTransactionResponse {

    private final String id;
    private final String paymentOrderId;
    private final String transactionId;
    private final PaymentStatus status;
    private final Instant createdAt;

    public PaymentTransactionResponse(String id, String paymentOrderId, String transactionId,
                                       PaymentStatus status, Instant createdAt) {
        this.id = id;
        this.paymentOrderId = paymentOrderId;
        this.transactionId = transactionId;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static PaymentTransactionResponse from(PaymentTransaction paymentTransaction) {
        return new PaymentTransactionResponse(
                paymentTransaction.getId(),
                paymentTransaction.getPaymentOrderId(),
                paymentTransaction.getTransactionId(),
                paymentTransaction.getStatus(),
                paymentTransaction.getCreatedAt()
        );
    }

    public String getId() {
        return id;
    }

    public String getPaymentOrderId() {
        return paymentOrderId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
