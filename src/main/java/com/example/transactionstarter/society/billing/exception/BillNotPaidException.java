package com.example.transactionstarter.society.billing.exception;

/** Thrown when a receipt is requested for a bill that has not been paid yet. */
public class BillNotPaidException extends RuntimeException {

    public BillNotPaidException(String id) {
        super("No receipt available - bill is not paid: " + id);
    }
}
