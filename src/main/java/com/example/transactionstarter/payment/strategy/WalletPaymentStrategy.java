package com.example.transactionstarter.payment.strategy;

import com.example.transactionstarter.payment.domain.BankAccount;
import com.example.transactionstarter.payment.domain.PaymentMethod;
import com.example.transactionstarter.payment.exception.InvalidPaymentInstrumentException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/** WALLET: account reference must be a non-blank alphanumeric wallet ID. */
@Component
public class WalletPaymentStrategy implements PaymentStrategy {

    @Override
    public PaymentMethod supportedMethod() {
        return PaymentMethod.WALLET;
    }

    @Override
    public boolean process(BankAccount senderAccount, BankAccount receiverAccount, BigDecimal amount) {
        validateWalletId(senderAccount.getAccountRef());
        validateWalletId(receiverAccount.getAccountRef());
        return !PaymentSimulation.isSimulatedFailure(senderAccount.getAccountRef());
    }

    private void validateWalletId(String accountRef) {
        if (accountRef == null || !accountRef.matches("[A-Za-z0-9]+")) {
            throw new InvalidPaymentInstrumentException("Wallet account reference must be alphanumeric");
        }
    }
}
