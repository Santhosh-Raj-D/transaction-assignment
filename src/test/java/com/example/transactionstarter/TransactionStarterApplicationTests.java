package com.example.transactionstarter;

import com.example.transactionstarter.transaction.domain.CurrencyCode;
import com.example.transactionstarter.transaction.domain.Transaction;
import com.example.transactionstarter.transaction.domain.TransactionStatus;
import com.example.transactionstarter.transaction.domain.TransactionType;
import com.example.transactionstarter.transaction.dto.CreateTransactionRequest;
import com.example.transactionstarter.transaction.dto.UpdateStatusRequest;
import com.example.transactionstarter.transaction.repository.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Covers the four minimum cases required by the assignment brief (Section 6):
 *   1. A transaction created successfully
 *   2. A transaction rejected because it fails validation
 *   3. A duplicate Transaction ID rejected
 *   4. A request for a transaction that does not exist
 * Along with Sample Test Case.
 * Plus two extra cases covering the status state machine and customer-scoped lookup.
 */
@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TransactionRepository transactionRepository;

    @BeforeEach
    void cleanDatabase() {
        transactionRepository.deleteAll();
    }

    @Test
    void contextLoads() {
    }

    // 1. A transaction created successfully
    @Test
    void createTransaction_withValidRequest_returns201AndPendingStatus() throws Exception {
        CreateTransactionRequest request = new CreateTransactionRequest(
                "txn-001", "customer-1", new BigDecimal("100.00"), CurrencyCode.INR, TransactionType.PAYMENT);

        mockMvc.perform(post("/api/transactions")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("txn-001"))
                .andExpect(jsonPath("$.customerId").value("customer-1"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    // 2. A transaction rejected because it fails validation
    @Test
    void createTransaction_withNegativeAmount_returns400WithFieldError() throws Exception {
        CreateTransactionRequest request = new CreateTransactionRequest(
                "txn-002", "customer-1", new BigDecimal("-5.00"), CurrencyCode.INR, TransactionType.PAYMENT);

        mockMvc.perform(post("/api/transactions")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.amount").exists());
    }

    // 3. A duplicate Transaction ID rejected
    @Test
    void createTransaction_withDuplicateId_returns409() throws Exception {
        transactionRepository.save(
                new Transaction("txn-dup", "customer-1", new BigDecimal("10.00"), CurrencyCode.INR, TransactionType.PAYMENT));

        CreateTransactionRequest request = new CreateTransactionRequest(
                "txn-dup", "customer-1", new BigDecimal("20.00"), CurrencyCode.INR, TransactionType.PAYMENT);

        mockMvc.perform(post("/api/transactions")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    // 4. A request for a transaction that does not exist
    @Test
    void getTransaction_whenNotFound_returns404() throws Exception {
        mockMvc.perform(get("/api/transactions/does-not-exist"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateStatus_withIllegalTransition_returns409() throws Exception {
        // A freshly created transaction starts at PENDING. PENDING -> REVERSED
        // is not a legal transition (REVERSED is only reachable from COMPLETED).
        transactionRepository.save(
                new Transaction("txn-003", "customer-2", new BigDecimal("50.00"), CurrencyCode.USD, TransactionType.PAYMENT));

        UpdateStatusRequest request = new UpdateStatusRequest(TransactionStatus.REVERSED);

        mockMvc.perform(patch("/api/transactions/txn-003/status")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void updateStatus_withLegalTransition_returns200AndNewStatus() throws Exception {
        transactionRepository.save(
                new Transaction("txn-004", "customer-2", new BigDecimal("50.00"), CurrencyCode.USD, TransactionType.PAYMENT));

        UpdateStatusRequest request = new UpdateStatusRequest(TransactionStatus.COMPLETED);

        mockMvc.perform(patch("/api/transactions/txn-004/status")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void getTransactionsForCustomer_returnsOnlyThatCustomersTransactions() throws Exception {
        transactionRepository.save(new Transaction("txn-005", "customer-A", new BigDecimal("10.00"), CurrencyCode.INR, TransactionType.PAYMENT));
        transactionRepository.save(new Transaction("txn-006", "customer-A", new BigDecimal("20.00"), CurrencyCode.INR, TransactionType.REFUND));
        transactionRepository.save(new Transaction("txn-007", "customer-B", new BigDecimal("30.00"), CurrencyCode.EUR, TransactionType.TRANSFER));

        mockMvc.perform(get("/api/customers/customer-A/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }
}
