package com.example.transactionstarter.CustomTests;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
 * Covers the new admin-facing Bill APIs added in update-3:
 * cancel a bill, list a resident's outstanding bills, and filter bills
 * by status across all residents.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SocietyBillControllerCustomTests {

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

    private String createBill(String residentId, String description) throws Exception {
        String body = """
                {"residentId": "%s", "raisedBy": "ADM-1", "head": "MAINTENANCE", "description": "%s", "amount": 750.00, "currency": "INR"}
                """.formatted(residentId, description);
        MvcResult result = mockMvc.perform(post("/api/society/bills")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(), "$.id");
    }

    @Test
    void cancelPendingBill_setsStatusToCancelled() throws Exception {
        String residentId = createResident("BILL-CANCEL-1");
        String billId = createBill(residentId, "Cancel me");

        mockMvc.perform(patch("/api/society/bills/{id}/cancel", billId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void cancelAlreadyPaidBill_isRejected() throws Exception {
        String residentId = createResident("BILL-CANCEL-2");
        String billId = createBill(residentId, "Pay then cancel");

        mockMvc.perform(post("/api/society/bills/{id}/pay", billId))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/api/society/bills/{id}/cancel", billId))
                .andExpect(status().isConflict());
    }

    @Test
    void cancelNonExistentBill_returnsNotFound() throws Exception {
        mockMvc.perform(patch("/api/society/bills/{id}/cancel", "BILL-DOES-NOT-EXIST"))
                .andExpect(status().isNotFound());
    }

    @Test
    void outstandingBills_returnsOnlyPending() throws Exception {
        String residentId = createResident("BILL-OUT-1");
        String paidBillId = createBill(residentId, "Will be paid");
        createBill(residentId, "Stays pending");

        mockMvc.perform(post("/api/society/bills/{id}/pay", paidBillId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/society/residents/{residentId}/bills/outstanding", residentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    void outstandingBills_forResidentWithNonePending_returnsEmptyList() throws Exception {
        String residentId = createResident("BILL-OUT-2");

        mockMvc.perform(get("/api/society/residents/{residentId}/bills/outstanding", residentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void filterBillsByStatus_returnsOnlyMatching() throws Exception {
        String residentId = createResident("BILL-FILTER-1");
        createBill(residentId, "Filter target");

        mockMvc.perform(get("/api/society/bills").param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }
}
