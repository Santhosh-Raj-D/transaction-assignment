package com.example.transactionstarter.CustomTests;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
 * Covers the new POST /api/transactions/{id}/refund endpoint added in
 * update-3. It reuses the existing status state-machine
 * (COMPLETED -&gt; REVERSED) rather than a separate refund algorithm.
 */
@SpringBootTest
@AutoConfigureMockMvc
class TransactionRefundControllerCustomTests {

    @Autowired
    private MockMvc mockMvc;

    private void createTransaction(String id) throws Exception {
        String body = """
                {"transactionId": "%s", "customerId": "CUST-REFUND", "amount": 250.00, "currency": "INR", "type": "PAYMENT"}
                """.formatted(id);
        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
    }

    @Test
    void refund_completedTransaction_becomesReversed() throws Exception {
        String id = "TXN-REFUND-OK";
        createTransaction(id);
        mockMvc.perform(patch("/api/transactions/{id}/status", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\": \"COMPLETED\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/transactions/{id}/refund", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REVERSED"));
    }

    @Test
    void refund_pendingTransaction_isRejected() throws Exception {
        String id = "TXN-REFUND-PENDING";
        createTransaction(id);

        mockMvc.perform(post("/api/transactions/{id}/refund", id))
                .andExpect(status().isConflict());
    }
}
