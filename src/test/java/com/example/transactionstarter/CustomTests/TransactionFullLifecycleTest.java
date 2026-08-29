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
 * Full lifecycle API test for a transaction.
 *
 * PENDING -> COMPLETED -> REVERSED
 * 
 * It also verifies that REVERSED is a terminal state and cannot
 * transition to another status.
 *
 * 
 * Expected lifecycle:
 *
 *                    ┌───────────────┐
 *                    │    PENDING    │
 *                    └───────┬───────┘
 *                            │
 *                            │ COMPLETED
 *                            ▼
 *                    ┌───────────────┐
 *                    │   COMPLETED   │
 *                    └───────┬───────┘
 *                            │
 *                            │ REVERSED
 *                            ▼
 *                    ┌───────────────┐
 *                    │   REVERSED    │
 *                    └───────┬───────┘
 *                            │
 *                         BLOCKED
 *                            │
 *                   ┌────────┼────────┐
 *                   ▼        ▼        ▼
 *                PENDING  COMPLETED  FAILED
 *                     ALL REJECTED
 * 
 */
@SpringBootTest
@AutoConfigureMockMvc
class TransactionFullLifecycleTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Tests the complete transaction lifecycle:
     *
     * 1. Create transaction
     * 2. Verify initial status is PENDING
     * 3. PENDING -> COMPLETED
     * 4. COMPLETED -> REVERSED
     * 5. Verify REVERSED -> PENDING is blocked
     * 6. Verify REVERSED -> COMPLETED is blocked
     * 7. Verify REVERSED -> FAILED is blocked
     * 8. Verify REVERSED -> REVERSED is blocked
     */
    @Test
    void fullTransactionLifecycle_ShouldEndAtReversed() throws Exception {

        /*
         * ---------------------------------------------------------
         * STEP 1: CREATE TRANSACTION
         * ---------------------------------------------------------
         */
        String transactionId = "TXN-LIFECYCLE-001";

        String createRequest = """
                {
                    "transactionId": "TXN-LIFECYCLE-001",
                    "customerId": "CUST001",
                    "amount": 500.00,
                    "currency": "INR",
                    "type": "PAYMENT"
                }
                """;

        mockMvc.perform(
                post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRequest)
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(transactionId))
                .andExpect(jsonPath("$.status").value("PENDING"));


        /*
         * ---------------------------------------------------------
         * STEP 2: PENDING -> COMPLETED
         * ---------------------------------------------------------
         *
         * This is a valid transition.
         *
         * According to TransactionService:
         *
         * PENDING -> COMPLETED
         *
         * Therefore the API should return 200 OK.
         */
        String completedRequest = """
                {
                    "status": "COMPLETED"
                }
                """;

        mockMvc.perform(
                patch("/api/transactions/{id}/status", transactionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(completedRequest)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transactionId))
                .andExpect(jsonPath("$.status").value("COMPLETED"));


        /*
         * ---------------------------------------------------------
         * STEP 3: COMPLETED -> REVERSED
         * ---------------------------------------------------------
         *
         * This is also a valid transition.
         *
         * According to TransactionService:
         *
         * COMPLETED -> REVERSED
         *
         * Therefore the API should return 200 OK.
         */
        String reversedRequest = """
                {
                    "status": "REVERSED"
                }
                """;

        mockMvc.perform(
                patch("/api/transactions/{id}/status", transactionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reversedRequest)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transactionId))
                .andExpect(jsonPath("$.status").value("REVERSED"));


        /*
         * ---------------------------------------------------------
         * STEP 4: REVERSED -> PENDING
         * ---------------------------------------------------------
         *
         * REVERSED is a terminal state.
         *
         * According to TransactionService:
         *
         * REVERSED -> nothing
         *
         * Therefore this request must be rejected.
         *
         * Expected:
         * HTTP 409 Conflict
         */
        String pendingRequest = """
                {
                    "status": "PENDING"
                }
                """;

        mockMvc.perform(
                patch("/api/transactions/{id}/status", transactionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pendingRequest)
        )
                .andExpect(status().isConflict());


        /*
         * ---------------------------------------------------------
         * STEP 5: REVERSED -> COMPLETED
         * ---------------------------------------------------------
         *
         * A reversed transaction cannot become completed again.
         *
         * Expected:
         * HTTP 409 Conflict
         */
        String completedAgainRequest = """
                {
                    "status": "COMPLETED"
                }
                """;

        mockMvc.perform(
                patch("/api/transactions/{id}/status", transactionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(completedAgainRequest)
        )
                .andExpect(status().isConflict());


        /*
         * ---------------------------------------------------------
         * STEP 6: REVERSED -> FAILED
         * ---------------------------------------------------------
         *
         * A reversed transaction cannot become failed.
         *
         * Expected:
         * HTTP 409 Conflict
         */
        String failedRequest = """
                {
                    "status": "FAILED"
                }
                """;

        mockMvc.perform(
                patch("/api/transactions/{id}/status", transactionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(failedRequest)
        )
                .andExpect(status().isConflict());


        /*
         * ---------------------------------------------------------
         * STEP 7: REVERSED -> REVERSED
         * ---------------------------------------------------------
         *
         * Even attempting to set the transaction to REVERSED again
         * is rejected because REVERSED has no allowed outgoing
         * transitions.
         *
         * Expected:
         * HTTP 409 Conflict
         */
        String reversedAgainRequest = """
                {
                    "status": "REVERSED"
                }
                """;

        mockMvc.perform(
                patch("/api/transactions/{id}/status", transactionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reversedAgainRequest)
        )
                .andExpect(status().isConflict());
    }
}