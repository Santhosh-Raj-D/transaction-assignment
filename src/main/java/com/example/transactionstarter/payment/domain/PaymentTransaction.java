package com.example.transactionstarter.payment.domain;

import com.example.transactionstarter.idgeneration.service.IdGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * Represents the actual payment attempt/result for a {@link PaymentOrder} -
 * "what happened when we tried to process this payment" - including the
 * ID of the underlying {@code Transaction} it produced, if any.
 *
 * {@code transactionId} is nullable: a payment can fail during strategy
 * validation before any underlying Transaction is ever created.
 */
@Entity
@Table(name = "payment_transactions")
public class PaymentTransaction {

    @Id
    @Column(updatable = false, nullable = false)
    private String id;

    @Column(nullable = false)
    private String paymentOrderId;

    @Column
    private String transactionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected PaymentTransaction() {
        // required by JPA
    }

    public PaymentTransaction(String paymentOrderId, String transactionId, PaymentStatus status) {
        this.id = IdGenerator.generatePaymentTransactionId(paymentOrderId, transactionId == null ? "NA" : transactionId);
        this.paymentOrderId = paymentOrderId;
        this.transactionId = transactionId;
        this.status = status;
        this.createdAt = Instant.now();
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
