package com.example.transactionstarter.CustomTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.transactionstarter.payment.repository.BankAccountRepository;
import com.example.transactionstarter.payment.repository.BankRepository;

/**
 * Verifies Bank/BankAccount are looked up and reused on repeat use, not
 * recreated on every payment.
 */
@SpringBootTest
@AutoConfigureMockMvc
class PaymentBankReuseCustomTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Test
    void twoPaymentsSameBankAndInstrument_reuseExistingBankAndAccount() throws Exception {
        String requestBody = """
                {
                    "payerId": "RES-REUSE-1",
                    "payeeId": "MER-REUSE-1",
                    "amount": 10.00,
                    "currency": "INR",
                    "method": "UPI",
                    "senderBankName": "ReuseBank",
                    "senderAccountRef": "reuseSender@bank",
                    "receiverBankName": "ReuseBank",
                    "receiverAccountRef": "reuseReceiver@bank"
                }
                """;

        long banksBefore = bankRepository.count();
        long accountsBefore = bankAccountRepository.count();

        mockMvc.perform(post("/api/payments").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/payments").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isCreated());

        // "ReuseBank" is both the sender and receiver bank across 2 calls - should be created once, not 4 times.
        assertEquals(banksBefore + 1, bankRepository.count());
        // 2 distinct (bank, method, accountRef) instruments - created once each, reused on the second call.
        assertEquals(accountsBefore + 2, bankAccountRepository.count());
    }
}
