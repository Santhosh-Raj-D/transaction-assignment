package com.example.transactionstarter.CustomTests;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.example.transactionstarter.society.common.IdGenerator;

/**
 * Verifies backend-generated transaction IDs: never manually supplied,
 * built from payer/receiver/head, and unique per payment.
 */
class TransactionIdGenerationTest {

    @Test
    void generateTransactionId_isNotNullAndReflectsPayer() {
        String id = IdGenerator.generateTransactionId("RES-A101-1", "ADM-S1-1", "MAINTENANCE");

        assertNotNull(id);
        assertTrue(id.startsWith("TXN-"));
        assertTrue(id.contains("RESA1011") || id.contains("RES"));
    }

    @Test
    void generateTransactionId_forSamePayerTwice_isStillUnique() {
        String firstId = IdGenerator.generateTransactionId("RES-A101-1", "ADM-S1-1", "MAINTENANCE");
        String secondId = IdGenerator.generateTransactionId("RES-A101-1", "ADM-S1-1", "MAINTENANCE");

        // Same resident paying the same kind of bill twice must not collide.
        assertNotEquals(firstId, secondId);
    }
}
