package com.example.transactionstarter.payment.domain;

/**
 * The instrument used to make a payment. Each value has a matching
 * {@code PaymentStrategy} implementation in the {@code payment.strategy} package.
 */
public enum PaymentMethod {
    UPI,
    CARD,
    WALLET,
    NET_BANKING
}
