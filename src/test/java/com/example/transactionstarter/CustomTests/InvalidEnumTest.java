package com.example.transactionstarter.CustomTests;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * {
 *     "transactionId": "TXN-ENUM-001",
 *     "customerId": "CUST001",
 *     "amount": 500.00,
 *     "currency": "ZZZ",
 *     "type": "PAYMENT"
 * }

 * "ZZZ" is not a valid CurrencyCode enum value.
 */
@SpringBootTest
@AutoConfigureMockMvc
class InvalidEnumTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Verifies that an invalid currency enum value returns
     * HTTP 400 Bad Request instead of causing an application crash.
     */
    @Test
    void invalidCurrencyEnum_ShouldReturnBadRequest() throws Exception {

        String requestJson = """
                {
                    "transactionId": "TXN-ENUM-001",
                    "customerId": "CUST001",
                    "amount": 500.00,
                    "currency": "ZZZ",
                    "type": "PAYMENT"
                }
                """;

        mockMvc.perform(
                post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error")
                        .value("Malformed request body or invalid field value"));
    }
}