package com.example.transactionstarter.payment.strategy;

import com.example.transactionstarter.payment.domain.BankAccount;
import com.example.transactionstarter.payment.domain.PaymentMethod;
import com.example.transactionstarter.payment.exception.InvalidPaymentInstrumentException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/** UPI: account reference must look like a VPA (contains "@"), e.g. "resident@bank". */
@Component
public class UpiPaymentStrategy implements PaymentStrategy {

    @Override
    public PaymentMethod supportedMethod() {
        return PaymentMethod.UPI;
    }

    @Override
    public boolean process(BankAccount senderAccount, BankAccount receiverAccount, BigDecimal amount) {
        validateVpa(senderAccount.getAccountRef());
        validateVpa(receiverAccount.getAccountRef());
        return !PaymentSimulation.isSimulatedFailure(senderAccount.getAccountRef());
    }

    private void validateVpa(String accountRef) {
        if (accountRef == null || !accountRef.contains("@")) {
            throw new InvalidPaymentInstrumentException("UPI account reference must be a VPA containing '@'");
        }
    }
}
