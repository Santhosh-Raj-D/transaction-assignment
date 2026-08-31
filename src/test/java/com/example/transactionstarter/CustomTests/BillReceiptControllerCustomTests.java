package com.example.transactionstarter.CustomTests;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Covers the new GET /api/society/bills/{id}/receipt endpoint added in
 * update-3.
 */
@SpringBootTest
@AutoConfigureMockMvc
class BillReceiptControllerCustomTests {

    @Autowired
    private MockMvc mockMvc;

    private String createResident(String flatId) throws Exception {
        String body = """
                {"name": "Resident %s", "flatId": "%s", "contact": "9000000000"}
                """.formatted(flatId, flatId);
        MvcResult result = mockMvc.perform(post("/api/society/residents")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(), "$.id");
    }

    private String createBill(String residentId) throws Exception {
        String body = """
                {"residentId": "%s", "raisedBy": "ADM-1", "head": "MAINTENANCE", "description": "Receipt test", "amount": 900.00, "currency": "INR"}
                """.formatted(residentId);
        MvcResult result = mockMvc.perform(post("/api/society/bills")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(), "$.id");
    }

    @Test
    void receipt_forPaidBill_includesTransactionDetails() throws Exception {
        String residentId = createResident("RECEIPT-1");
        String billId = createBill(residentId);

        mockMvc.perform(post("/api/society/bills/{id}/pay", billId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/society/bills/{id}/receipt", billId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.billId").value(billId))
                .andExpect(jsonPath("$.transactionId").isNotEmpty());
    }

    @Test
    void receipt_forUnpaidBill_isRejected() throws Exception {
        String residentId = createResident("RECEIPT-2");
        String billId = createBill(residentId);

        mockMvc.perform(get("/api/society/bills/{id}/receipt", billId))
                .andExpect(status().isConflict());
    }
}
