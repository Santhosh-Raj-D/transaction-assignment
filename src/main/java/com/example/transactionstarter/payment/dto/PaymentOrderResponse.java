package com.example.transactionstarter.payment.dto;

import com.example.transactionstarter.payment.domain.PaymentMethod;
import com.example.transactionstarter.payment.domain.PaymentOrder;
import com.example.transactionstarter.payment.domain.PaymentStatus;
import com.example.transactionstarter.transaction.domain.CurrencyCode;

import java.math.BigDecimal;
import java.time.Instant;

/** What the client receives back when fetching a payment order by ID. */
public class PaymentOrderResponse {

    private final String id;
    private final String payerId;
    private final String payeeId;
    private final BigDecimal amount;
    private final CurrencyCode currency;
    private final PaymentMethod method;
    private final PaymentStatus status;
    private final Instant createdAt;
    private final Instant updatedAt;

    public PaymentOrderResponse(String id, String payerId, String payeeId, BigDecimal amount, CurrencyCode currency,
                                 PaymentMethod method, PaymentStatus status, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.payerId = payerId;
        this.payeeId = payeeId;
        this.amount = amount;
        this.currency = currency;
        this.method = method;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static PaymentOrderResponse from(PaymentOrder order) {
        return new PaymentOrderResponse(
                order.getId(),
                order.getPayerId(),
                order.getPayeeId(),
                order.getAmount(),
                order.getCurrency(),
                order.getMethod(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }

    public String getId() {
        return id;
    }

    public String getPayerId() {
        return payerId;
    }

    public String getPayeeId() {
        return payeeId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public CurrencyCode getCurrency() {
        return currency;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
