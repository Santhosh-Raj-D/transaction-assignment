package com.example.transactionstarter.payment.strategy;

import com.example.transactionstarter.payment.domain.BankAccount;
import com.example.transactionstarter.payment.domain.PaymentMethod;
import com.example.transactionstarter.payment.exception.InvalidPaymentInstrumentException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/** NET_BANKING: account reference must be a non-blank alphanumeric/hyphen account reference token. */
@Component
public class NetBankingPaymentStrategy implements PaymentStrategy {

    @Override
    public PaymentMethod supportedMethod() {
        return PaymentMethod.NET_BANKING;
    }

    @Override
    public boolean process(BankAccount senderAccount, BankAccount receiverAccount, BigDecimal amount) {
        validateAccountRef(senderAccount.getAccountRef());
        validateAccountRef(receiverAccount.getAccountRef());
        return !PaymentSimulation.isSimulatedFailure(senderAccount.getAccountRef());
    }

    private void validateAccountRef(String accountRef) {
        if (accountRef == null || !accountRef.matches("[A-Za-z0-9-]+")) {
            throw new InvalidPaymentInstrumentException("Net banking account reference must be alphanumeric/hyphen");
        }
    }
}
