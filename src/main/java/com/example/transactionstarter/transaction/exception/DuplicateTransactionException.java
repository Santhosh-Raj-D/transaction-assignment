package com.example.transactionstarter.transaction.exception;

public class DuplicateTransactionException extends RuntimeException {

    public DuplicateTransactionException(String id) {
        super("Transaction already exists: " + id);
    }
}
