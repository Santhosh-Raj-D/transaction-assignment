package com.example.transactionstarter.society.billing.exception;

/** Thrown when a Bill cannot be found by ID. */
public class BillNotFoundException extends RuntimeException {

    public BillNotFoundException(String id) {
        super("Bill not found: " + id);
    }
}
