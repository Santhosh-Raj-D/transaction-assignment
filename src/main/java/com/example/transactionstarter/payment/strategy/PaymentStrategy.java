package com.example.transactionstarter.payment.strategy;

import com.example.transactionstarter.payment.domain.BankAccount;
import com.example.transactionstarter.payment.domain.PaymentMethod;

import java.math.BigDecimal;

/**
 * Method-specific payment processing behind a common interface. Each
 * implementation validates its account reference's format and simulates
 * processing - no real gateway calls are made, this is a demo backend.
 */
public interface PaymentStrategy {

    /** The PaymentMethod this strategy handles. */
    PaymentMethod supportedMethod();

    /**
     * Validates the sender/receiver instruments for this method and
     * simulates processing.
     *
     * @throws com.example.transactionstarter.payment.exception.InvalidPaymentInstrumentException
     *         if either account reference does not fit this method's expected format
     * @return true if the simulated processing succeeded, false if it failed
     *         (a business failure, not a format error)
     */
    boolean process(BankAccount senderAccount, BankAccount receiverAccount, BigDecimal amount);
}
