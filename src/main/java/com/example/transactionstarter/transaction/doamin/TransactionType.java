package com.example.transactionstarter.transaction.doamin;

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
