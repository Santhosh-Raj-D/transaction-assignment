package com.example.transactionstarter.transaction.exception;

import com.example.transactionstarter.transaction.domain.TransactionStatus;

public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(TransactionStatus from, TransactionStatus to) {
        super("Cannot transition transaction from " + from + " to " + to);
    }
}
