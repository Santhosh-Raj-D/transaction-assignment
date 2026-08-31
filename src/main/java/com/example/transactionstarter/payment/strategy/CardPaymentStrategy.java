package com.example.transactionstarter.payment.strategy;

import com.example.transactionstarter.payment.domain.BankAccount;
import com.example.transactionstarter.payment.domain.PaymentMethod;
import com.example.transactionstarter.payment.exception.InvalidPaymentInstrumentException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/** CARD: account reference must be a masked/demo reference ending with the last 4 digits, e.g. "****1234". */
@Component
public class CardPaymentStrategy implements PaymentStrategy {

    @Override
    public PaymentMethod supportedMethod() {
        return PaymentMethod.CARD;
    }

    @Override
    public boolean process(BankAccount senderAccount, BankAccount receiverAccount, BigDecimal amount) {
        validateMaskedCard(senderAccount.getAccountRef());
        validateMaskedCard(receiverAccount.getAccountRef());
        return !PaymentSimulation.isSimulatedFailure(senderAccount.getAccountRef());
    }

    private void validateMaskedCard(String accountRef) {
        if (accountRef == null || accountRef.length() < 4 || !accountRef.substring(accountRef.length() - 4).matches("\\d{4}")) {
            throw new InvalidPaymentInstrumentException("Card account reference must end with the last 4 digits");
        }
    }
}
