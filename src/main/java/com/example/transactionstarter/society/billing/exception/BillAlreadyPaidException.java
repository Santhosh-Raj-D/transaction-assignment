package com.example.transactionstarter.society.billing.exception;

/** Thrown when attempting to pay a Bill that is not in PENDING status. */
public class BillAlreadyPaidException extends RuntimeException {

    public BillAlreadyPaidException(String id) {
        super("Bill is not payable, current status does not allow payment: " + id);
    }
}
