package com.example.transactionstarter.idgeneration.exception;

/**
 * Thrown when an ID cannot be generated because none of the meaningful
 * fields supplied for it contain usable information.
 */
public class InvalidIdInputException extends RuntimeException {

    public InvalidIdInputException(String message) {
        super(message);
    }
}
