package com.example.transactionstarter.payment.dto;

import com.example.transactionstarter.payment.domain.PaymentMethod;
import com.example.transactionstarter.payment.domain.PaymentOrder;
import com.example.transactionstarter.payment.domain.PaymentStatus;
import com.example.transactionstarter.payment.domain.PaymentTransaction;
import com.example.transactionstarter.transaction.domain.CurrencyCode;

import java.math.BigDecimal;

/** What the client receives back after a payment is created and processed. */
public class PaymentResponse {

    private final String paymentOrderId;
    private final String paymentTransactionId;
    private final String transactionId;
    private final PaymentStatus status;
    private final BigDecimal amount;
    private final CurrencyCode currency;
    private final PaymentMethod method;

    public PaymentResponse(String paymentOrderId, String paymentTransactionId, String transactionId,
                            PaymentStatus status, BigDecimal amount, CurrencyCode currency, PaymentMethod method) {
        this.paymentOrderId = paymentOrderId;
        this.paymentTransactionId = paymentTransactionId;
        this.transactionId = transactionId;
        this.status = status;
        this.amount = amount;
        this.currency = currency;
        this.method = method;
    }

    public static PaymentResponse from(PaymentOrder order, PaymentTransaction paymentTransaction) {
        return new PaymentResponse(
                order.getId(),
                paymentTransaction.getId(),
                paymentTransaction.getTransactionId(),
                order.getStatus(),
                order.getAmount(),
                order.getCurrency(),
                order.getMethod()
        );
    }

    public String getPaymentOrderId() {
        return paymentOrderId;
    }

    public String getPaymentTransactionId() {
        return paymentTransactionId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public PaymentStatus getStatus() {
        return status;
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
}
