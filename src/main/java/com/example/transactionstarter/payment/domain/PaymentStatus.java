package com.example.transactionstarter.payment.domain;

/**
 * Lifecycle status of a {@link PaymentOrder} or {@link PaymentTransaction}.
 *
 *   INITIATED  -> the order was created, processing not yet attempted
 *   SUCCESS    -> the chosen PaymentStrategy processed it and a COMPLETED
 *                 Transaction was recorded
 *   FAILED     -> the chosen PaymentStrategy rejected it, or the resulting
 *                 Transaction was marked FAILED
 */
public enum PaymentStatus {
    INITIATED,
    SUCCESS,
    FAILED
}
