package com.example.transactionstarter.payment.exception;

/** Thrown when a bank account's {@code accountRef} does not fit the format expected by its PaymentMethod. */
public class InvalidPaymentInstrumentException extends RuntimeException {

    public InvalidPaymentInstrumentException(String message) {
        super(message);
    }
}
