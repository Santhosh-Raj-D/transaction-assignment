package com.example.transactionstarter.society.billing.domain;

/**
 * Lifecycle status of a Bill.
 *   PENDING -> PAID (payment succeeds)
 *   PENDING -> CANCELLED (raised in error / waived)
 */
public enum BillStatus {
    PENDING,
    PAID,
    CANCELLED
}
