package com.example.transactionstarter.CustomTests;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.example.transactionstarter.idgeneration.service.IdGenerator;

/**
 * Verifies backend-generated payment-related IDs: never manually supplied,
 * and unique across repeated calls with the same input fields.
 */
class PaymentIdGenerationTest {

    @Test
    void generatePaymentOrderId_startsWithPayPrefix() {
        String id = IdGenerator.generatePaymentOrderId("RES-A101-1", "MER-GROC-1", "UPI");

        assertNotNull(id);
        assertTrue(id.startsWith("PAY-"));
    }

    @Test
    void generatePaymentOrderId_forSamePayerTwice_isStillUnique() {
        String firstId = IdGenerator.generatePaymentOrderId("RES-A101-1", "MER-GROC-1", "UPI");
        String secondId = IdGenerator.generatePaymentOrderId("RES-A101-1", "MER-GROC-1", "UPI");

        assertNotEquals(firstId, secondId);
    }

    @Test
    void generatePaymentTransactionId_startsWithPtxnPrefix() {
        String id = IdGenerator.generatePaymentTransactionId("PAY-001", "TXN-001");

        assertNotNull(id);
        assertTrue(id.startsWith("PTXN-"));
    }

    @Test
    void generateBankId_startsWithBankPrefix() {
        String id = IdGenerator.generateBankId("HDFC");

        assertNotNull(id);
        assertTrue(id.startsWith("BANK-"));
    }

    @Test
    void generateBankAccountId_startsWithBaccPrefix() {
        String id = IdGenerator.generateBankAccountId("BANK-1", "resident@hdfc");

        assertNotNull(id);
        assertTrue(id.startsWith("BACC-"));
    }
}
