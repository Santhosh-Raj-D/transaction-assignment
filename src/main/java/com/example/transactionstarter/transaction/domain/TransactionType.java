package com.example.transactionstarter.transaction.domain;

/**
 * The kind of money movement a transaction represents.
 */
public enum TransactionType {
    PAYMENT,
    REFUND,
    TRANSFER,
    DEPOSIT,
    WITHDRAWAL
}
