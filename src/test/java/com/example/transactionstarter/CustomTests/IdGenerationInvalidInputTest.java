package com.example.transactionstarter.CustomTests;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.example.transactionstarter.idgeneration.exception.InvalidIdInputException;
import com.example.transactionstarter.idgeneration.service.IdGenerator;

/**
 * Verifies the standalone idgeneration package rejects completely empty
 * input instead of silently producing a meaningless ID.
 */
class IdGenerationInvalidInputTest {

    @Test
    void generateResidentId_withNoUsableFields_throws() {
        assertThrows(InvalidIdInputException.class, () -> IdGenerator.generateResidentId(null, ""));
    }

    @Test
    void generateTransactionId_withNoUsableFields_throws() {
        assertThrows(InvalidIdInputException.class, () -> IdGenerator.generateTransactionId(" ", null, ""));
    }
}
