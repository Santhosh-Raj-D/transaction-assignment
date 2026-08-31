package com.example.transactionstarter.CustomTests;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.example.transactionstarter.payment.domain.BankAccount;
import com.example.transactionstarter.payment.domain.PaymentMethod;
import com.example.transactionstarter.payment.exception.InvalidPaymentInstrumentException;
import com.example.transactionstarter.payment.strategy.CardPaymentStrategy;
import com.example.transactionstarter.payment.strategy.NetBankingPaymentStrategy;
import com.example.transactionstarter.payment.strategy.UpiPaymentStrategy;
import com.example.transactionstarter.payment.strategy.WalletPaymentStrategy;

/**
 * Each PaymentStrategy: a correctly formatted instrument succeeds, a badly
 * formatted one is rejected, and a sender reference starting with "FAIL"
 * simulates a declined payment (deterministic, no real gateway).
 */
class PaymentStrategyTests {

    private static final BigDecimal AMOUNT = new BigDecimal("100.00");

    @Test
    void upiStrategy_validVpa_succeeds() {
        UpiPaymentStrategy strategy = new UpiPaymentStrategy();
        BankAccount sender = new BankAccount("BANK-1", PaymentMethod.UPI, "resident@hdfc");
        BankAccount receiver = new BankAccount("BANK-2", PaymentMethod.UPI, "merchant@icici");

        assertTrue(strategy.process(sender, receiver, AMOUNT));
    }

    @Test
    void upiStrategy_missingAtSymbol_throwsInvalidInstrument() {
        UpiPaymentStrategy strategy = new UpiPaymentStrategy();
        BankAccount sender = new BankAccount("BANK-1", PaymentMethod.UPI, "residenthdfc");
        BankAccount receiver = new BankAccount("BANK-2", PaymentMethod.UPI, "merchant@icici");

        assertThrows(InvalidPaymentInstrumentException.class, () -> strategy.process(sender, receiver, AMOUNT));
    }

    @Test
    void upiStrategy_failTriggerRef_returnsFalse() {
        UpiPaymentStrategy strategy = new UpiPaymentStrategy();
        BankAccount sender = new BankAccount("BANK-1", PaymentMethod.UPI, "fail@hdfc");
        BankAccount receiver = new BankAccount("BANK-2", PaymentMethod.UPI, "merchant@icici");

        assertFalse(strategy.process(sender, receiver, AMOUNT));
    }

    @Test
    void cardStrategy_validMaskedCard_succeeds() {
        CardPaymentStrategy strategy = new CardPaymentStrategy();
        BankAccount sender = new BankAccount("BANK-1", PaymentMethod.CARD, "****1234");
        BankAccount receiver = new BankAccount("BANK-2", PaymentMethod.CARD, "****5678");

        assertTrue(strategy.process(sender, receiver, AMOUNT));
    }

    @Test
    void cardStrategy_tooShortReference_throwsInvalidInstrument() {
        CardPaymentStrategy strategy = new CardPaymentStrategy();
        BankAccount sender = new BankAccount("BANK-1", PaymentMethod.CARD, "12");
        BankAccount receiver = new BankAccount("BANK-2", PaymentMethod.CARD, "****5678");

        assertThrows(InvalidPaymentInstrumentException.class, () -> strategy.process(sender, receiver, AMOUNT));
    }

    @Test
    void walletStrategy_validWalletId_succeeds() {
        WalletPaymentStrategy strategy = new WalletPaymentStrategy();
        BankAccount sender = new BankAccount("BANK-1", PaymentMethod.WALLET, "WALLET123");
        BankAccount receiver = new BankAccount("BANK-2", PaymentMethod.WALLET, "WALLET456");

        assertTrue(strategy.process(sender, receiver, AMOUNT));
    }

    @Test
    void walletStrategy_nonAlphanumericId_throwsInvalidInstrument() {
        WalletPaymentStrategy strategy = new WalletPaymentStrategy();
        BankAccount sender = new BankAccount("BANK-1", PaymentMethod.WALLET, "WALLET#123");
        BankAccount receiver = new BankAccount("BANK-2", PaymentMethod.WALLET, "WALLET456");

        assertThrows(InvalidPaymentInstrumentException.class, () -> strategy.process(sender, receiver, AMOUNT));
    }

    @Test
    void netBankingStrategy_validAccountRef_succeeds() {
        NetBankingPaymentStrategy strategy = new NetBankingPaymentStrategy();
        BankAccount sender = new BankAccount("BANK-1", PaymentMethod.NET_BANKING, "ACC-001");
        BankAccount receiver = new BankAccount("BANK-2", PaymentMethod.NET_BANKING, "ACC-002");

        assertTrue(strategy.process(sender, receiver, AMOUNT));
    }

    @Test
    void netBankingStrategy_blankReference_throwsInvalidInstrument() {
        NetBankingPaymentStrategy strategy = new NetBankingPaymentStrategy();
        BankAccount sender = new BankAccount("BANK-1", PaymentMethod.NET_BANKING, "");
        BankAccount receiver = new BankAccount("BANK-2", PaymentMethod.NET_BANKING, "ACC-002");

        assertThrows(InvalidPaymentInstrumentException.class, () -> strategy.process(sender, receiver, AMOUNT));
    }
}
