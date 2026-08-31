package com.example.transactionstarter.CustomTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.transactionstarter.payment.domain.BankAccount;
import com.example.transactionstarter.payment.repository.BankAccountRepository;
import com.example.transactionstarter.payment.repository.BankRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

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

    @PersistenceContext
    private EntityManager entityManager;

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

    /**
     * Confirms the BankAccount -&gt; Bank {@code @ManyToOne} mapping
     * actually navigates, not just that the annotation is present.
     */
    @Test
    @Transactional
    void bankAccount_canNavigateToBankViaJpaRelationship() throws Exception {
        String requestBody = """
                {
                    "payerId": "RES-JPA-1",
                    "payeeId": "MER-JPA-1",
                    "amount": 25.00,
                    "currency": "INR",
                    "method": "UPI",
                    "senderBankName": "JpaCheckBank",
                    "senderAccountRef": "jpaSender@bank",
                    "receiverBankName": "JpaCheckBank",
                    "receiverAccountRef": "jpaReceiver@bank"
                }
                """;

        mockMvc.perform(post("/api/payments").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isCreated());

        // The BankAccount above was created via `new BankAccount(...)` in this same
        // session, so its `bank` field was never populated. Flush + clear the
        // persistence context so the lookup below reloads a fresh managed instance
        // (with its lazy `bank` association) instead of returning that same object
        // from Hibernate's identity map.
        entityManager.flush();
        entityManager.clear();

        BankAccount account = bankAccountRepository.findByBankIdAndMethodAndAccountRef(
                        bankRepository.findByName("JpaCheckBank").orElseThrow().getId(),
                        com.example.transactionstarter.payment.domain.PaymentMethod.UPI,
                        "jpaSender@bank")
                .orElseThrow();

        assertNotNull(account.getBank());
        assertEquals("JpaCheckBank", account.getBank().getName());
    }
}
