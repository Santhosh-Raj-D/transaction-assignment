package com.example.transactionstarter.payment.exception;

import com.example.transactionstarter.payment.domain.PaymentMethod;

public class UnsupportedPaymentMethodException extends RuntimeException {

    public UnsupportedPaymentMethodException(PaymentMethod method) {
        super("Unsupported payment method: " + method);
    }
}
