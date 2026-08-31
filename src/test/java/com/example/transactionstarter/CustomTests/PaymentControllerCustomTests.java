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
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * End-to-end Payment API tests: create + process a payment (order creation,
 * strategy processing, and the resulting underlying Transaction, all in one
 * call), then fetch the order and its payment transaction back.
 */
@SpringBootTest
@AutoConfigureMockMvc
class PaymentControllerCustomTests {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createPayment_validUpiPayment_returnsSuccessAndCreatesTransaction() throws Exception {
        String requestBody = """
                {
                    "payerId": "RES-UPI-1",
                    "payeeId": "MER-UPI-1",
                    "amount": 250.00,
                    "currency": "INR",
                    "method": "UPI",
                    "senderBankName": "HDFC",
                    "senderAccountRef": "resident1@hdfc",
                    "receiverBankName": "ICICI",
                    "receiverAccountRef": "merchant1@icici"
                }
                """;

        MvcResult result = mockMvc.perform(
                post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.paymentOrderId").exists())
                .andExpect(jsonPath("$.transactionId").exists())
                .andReturn();

        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());
        String paymentOrderId = response.get("paymentOrderId").asText();

        // GET the order back
        mockMvc.perform(get("/api/payments/{id}", paymentOrderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.method").value("UPI"));

        // GET the payment transaction(s) for the order
        mockMvc.perform(get("/api/payments/{id}/transaction", paymentOrderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("SUCCESS"))
                .andExpect(jsonPath("$[0].transactionId").exists());
    }

    @Test
    void createPayment_simulatedFailureRef_returnsSuccessfulCreationButFailedStatus() throws Exception {
        String requestBody = """
                {
                    "payerId": "RES-UPI-2",
                    "payeeId": "MER-UPI-2",
                    "amount": 100.00,
                    "currency": "INR",
                    "method": "UPI",
                    "senderBankName": "HDFC",
                    "senderAccountRef": "fail@hdfc",
                    "receiverBankName": "ICICI",
                    "receiverAccountRef": "merchant2@icici"
                }
                """;

        mockMvc.perform(
                post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("FAILED"));
    }

    @Test
    void createPayment_invalidCardReference_returnsBadRequest() throws Exception {
        String requestBody = """
                {
                    "payerId": "RES-CARD-1",
                    "payeeId": "MER-CARD-1",
                    "amount": 500.00,
                    "currency": "INR",
                    "method": "CARD",
                    "senderBankName": "HDFC",
                    "senderAccountRef": "12",
                    "receiverBankName": "ICICI",
                    "receiverAccountRef": "****5678"
                }
                """;

        mockMvc.perform(
                post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPayment_missingRequiredField_returnsBadRequest() throws Exception {
        String requestBody = """
                {
                    "payeeId": "MER-WALLET-1",
                    "amount": 50.00,
                    "currency": "INR",
                    "method": "WALLET",
                    "senderBankName": "PayBank",
                    "senderAccountRef": "WALLET123",
                    "receiverBankName": "PayBank",
                    "receiverAccountRef": "WALLET456"
                }
                """;

        mockMvc.perform(
                post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPayment_invalidAmount_returnsBadRequest() throws Exception {
        String requestBody = """
                {
                    "payerId": "RES-AMT-1",
                    "payeeId": "MER-AMT-1",
                    "amount": 0.00,
                    "currency": "INR",
                    "method": "WALLET",
                    "senderBankName": "PayBank",
                    "senderAccountRef": "WALLET789",
                    "receiverBankName": "PayBank",
                    "receiverAccountRef": "WALLET012"
                }
                """;

        mockMvc.perform(
                post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPayment_nonExistentId_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/payments/{id}", "PAY-DOES-NOT-EXIST"))
                .andExpect(status().isNotFound());
    }
}
