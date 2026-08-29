package com.example.transactionstarter.CustomTests;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.transactionstarter.transaction.dto.CreateTransactionRequest;
import com.example.transactionstarter.transaction.domain.CurrencyCode;
import com.example.transactionstarter.transaction.domain.TransactionType;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Amount validation rules:
 *
 * Minimum amount = 0.01
 * Maximum amount = 1,000,000.00
 * 
 * Boundary values being tested:
 *
 * 
 * 0.01           -> PASS -> 201 Created
 * 0.00           -> FAIL -> 400 Bad Request
 * 1,000,000.00   -> PASS -> 201 Created
 * 1,000,000.01   -> FAIL -> 400 Bad Request
 * 
 */
@SpringBootTest
@AutoConfigureMockMvc
class TransactionAmountBoundaryTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Tests the exact minimum allowed amount.
     *
     * 0.01 is the minimum valid transaction amount,
     * therefore the request should be accepted.
     */
    @Test
    void amountExactlyMinimum_001_ShouldPass() throws Exception {

        CreateTransactionRequest request =
                new CreateTransactionRequest(
                        "Tnx111",
                        "CUST001",
                        new BigDecimal("0.01"),
                        CurrencyCode.INR,
                        TransactionType.PAYMENT
                );

        mockMvc.perform(
                post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
                .andExpect(status().isCreated());
    }

    /**
     * Tests the value immediately below the minimum.
     *
     * 0.00 is not greater than or equal to 0.01,
     * therefore the request should be rejected.
     */
    @Test
    void amountExactlyZero_000_ShouldFail() throws Exception {

        CreateTransactionRequest request =
                new CreateTransactionRequest(
                    "Tnx101",
                        "CUST001",
                        new BigDecimal("0.00"),
                        CurrencyCode.INR,
                        TransactionType.PAYMENT
                );

        mockMvc.perform(
                post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
                .andExpect(status().isBadRequest());
    }

    /**
     * Tests the exact maximum allowed amount.
     *
     * 1,000,000.00 is the maximum valid transaction amount,
     * therefore the request should be accepted.
     */
    @Test
    void amountExactlyMaximum_1000000_ShouldPass() throws Exception {

        CreateTransactionRequest request =
                new CreateTransactionRequest(
                    "Tnx101",
                        "CUST001",
                        new BigDecimal("1000000.00"),
                        CurrencyCode.INR,
                        TransactionType.PAYMENT
                );

        mockMvc.perform(
                post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
                .andExpect(status().isCreated());
    }

    /**
     * Tests the value immediately above the maximum.
     *
     * 1,000,000.01 exceeds the maximum allowed amount,
     * therefore the request should be rejected.
     */
    @Test
    void amountAboveMaximum_1000000_01_ShouldFail() throws Exception {

        CreateTransactionRequest request =
                new CreateTransactionRequest(
                    "Tnx101",
                        "CUST001",
                        new BigDecimal("1000000.01"),
                        CurrencyCode.INR,
                        TransactionType.PAYMENT
                );

        mockMvc.perform(
                post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
                .andExpect(status().isBadRequest());
    }
}