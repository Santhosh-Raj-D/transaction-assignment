package com.example.transactionstarter.CustomTests;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
 * Covers the new GET /api/transactions?status= and ?type= filter APIs
 * added in update-3. Reuses the existing TransactionRepository/Service.
 */
@SpringBootTest
@AutoConfigureMockMvc
class TransactionFilterControllerCustomTests {

    @Autowired
    private MockMvc mockMvc;

    private void createTransaction(String id, String customerId, String type) throws Exception {
        String body = """
                {"transactionId": "%s", "customerId": "%s", "amount": 100.00, "currency": "INR", "type": "%s"}
                """.formatted(id, customerId, type);
        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
    }

    @Test
    void filterByStatus_returnsOnlyMatchingStatusTransactions() throws Exception {
        createTransaction("TXN-FILTER-STATUS-1", "CUST-F1", "PAYMENT");

        mockMvc.perform(get("/api/transactions").param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id=='TXN-FILTER-STATUS-1')]").exists());
    }

    @Test
    void filterByStatus_withInvalidValue_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/transactions").param("status", "NOT_A_STATUS"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void filterByType_returnsOnlyMatchingTypeTransactions() throws Exception {
        createTransaction("TXN-FILTER-TYPE-1", "CUST-F2", "REFUND");

        mockMvc.perform(get("/api/transactions").param("type", "REFUND"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id=='TXN-FILTER-TYPE-1')]").exists());
    }
}
