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
 * Covers the new Society Admin / Property Manager / Security Guard
 * creation APIs added in update-3. These expose ActorService methods
 * that already existed but had no controller endpoint.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SocietyActorControllerCustomTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createAdmin_generatesIdAutomatically() throws Exception {
        String body = """
                {"name": "Treasurer One", "societyId": "SOC-1", "role": "TREASURER"}
                """;

        mockMvc.perform(post("/api/society/admins")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.role").value("TREASURER"));
    }

    @Test
    void createAdmin_missingRole_returnsValidationError() throws Exception {
        String body = """
                {"name": "Treasurer Two", "societyId": "SOC-1"}
                """;

        mockMvc.perform(post("/api/society/admins")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPropertyManager_generatesIdAutomatically() throws Exception {
        String body = """
                {"name": "Manager One", "societyId": "SOC-1"}
                """;

        mockMvc.perform(post("/api/society/property-managers")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    void createGuard_generatesIdAutomatically() throws Exception {
        String body = """
                {"name": "Guard One", "societyId": "SOC-1"}
                """;

        mockMvc.perform(post("/api/society/guards")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    void createResident_missingName_returnsBadRequest() throws Exception {
        String body = """
                {"flatId": "A-101", "contact": "9999999999"}
                """;

        mockMvc.perform(post("/api/society/residents")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createResident_missingContact_returnsBadRequest() throws Exception {
        String body = """
                {"name": "Asha Rao", "flatId": "A-102"}
                """;

        mockMvc.perform(post("/api/society/residents")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createMerchant_missingBusinessName_returnsBadRequest() throws Exception {
        String body = """
                {"category": "SALON", "settlementAccount": "merchant@upi"}
                """;

        mockMvc.perform(post("/api/society/merchants")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createMerchant_missingSettlementAccount_returnsBadRequest() throws Exception {
        String body = """
                {"businessName": "Glow Salon", "category": "SALON"}
                """;

        mockMvc.perform(post("/api/society/merchants")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }
}
