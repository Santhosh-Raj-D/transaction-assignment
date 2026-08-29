package com.example.transactionstarter.CustomTests;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * An empty transaction list is considered a successful request.
 * Therefore, the API should return 200 OK with an empty JSON array
 * rather than 404 Not Found.
 */
@SpringBootTest
@AutoConfigureMockMvc
class TransactionEmptyListTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Verifies that a customer with no transactions receives
     * HTTP 200 OK and an empty JSON array.
     *
     * This test is important because an empty result is not the
     * same thing as a missing resource.
     */
    @Test
    void customerWithNoTransactions_ShouldReturn200AndEmptyList()
            throws Exception {

        String customerId = "CUSTOMER-WITH-NO-TRANSACTIONS";

        mockMvc.perform(
                get("/api/customers/{customerId}/transactions", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON))
                .andExpect(content().json("[]"));
    }
}