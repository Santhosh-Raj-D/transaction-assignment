package com.example.transactionstarter.transaction.doamin;

/**
 * Lifecycle status of a transaction.
 *
 * Legal transitions are enforced by TransactionService, not here:
 *   PENDING   -> COMPLETED, FAILED
 *   FAILED    -> PENDING   (retry)
 *   COMPLETED -> REVERSED  (refund)
 *   REVERSED  -> (terminal - no further transitions)
 */
public enum TransactionStatus {
    PENDING,
    COMPLETED,
    FAILED,
    REVERSED
}
