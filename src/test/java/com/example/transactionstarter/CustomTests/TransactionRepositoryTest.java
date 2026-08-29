package com.example.transactionstarter.CustomTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.transactionstarter.transaction.domain.CurrencyCode;
import com.example.transactionstarter.transaction.domain.Transaction;
import com.example.transactionstarter.transaction.domain.TransactionType;
import com.example.transactionstarter.transaction.repository.TransactionRepository;

/**
 * Repository tests for TransactionRepository.
 *
 * @DataJpaTest loads only the JPA-related components required for
 * testing the persistence layer.
 *
 * Main flow:
 *
 * TransactionRepositoryTest
 *          |
 *          v
 * TransactionRepository
 *          |
 *          v
 *       JPA/Hibernate
 *          |
 *          v
 *       H2 Database
 * 
 */
@DataJpaTest
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository repository;

    /**
     * Tests that a transaction can be saved and then retrieved
     * using its primary key.
     *
     * Expected flow:
     *
     * Transaction
     *     |
     *     v
     * repository.save()
     *     |
     *     v
     * H2 Database
     *     |
     *     v
     * repository.findById()
     *     |
     *     v
     * Transaction found
     * 
     */
    @Test
    void saveAndFindById_ShouldReturnTransaction() {

        Transaction transaction = new Transaction(
                "TXN-REPO-001",
                "CUST001",
                new BigDecimal("500.00"),
                CurrencyCode.INR,
                TransactionType.PAYMENT
        );

        /*
         * Save the transaction into the H2 database.
         */
        Transaction savedTransaction = repository.save(transaction);

        /*
         * Verify that the transaction was successfully saved.
         */
        assertNotNull(savedTransaction);

        /*
         * Search for the transaction using its primary key.
         */
        Optional<Transaction> result =
                repository.findById("TXN-REPO-001");

        /*
         * Verify that the transaction exists.
         */
        assertTrue(result.isPresent());

        /*
         * Verify that the correct transaction was returned.
         */
        assertEquals(
                "TXN-REPO-001",
                result.get().getId()
        );

        assertEquals(
                "CUST001",
                result.get().getCustomerId()
        );

        assertEquals(
                new BigDecimal("500.00"),
                result.get().getAmount()
        );

        assertEquals(
                CurrencyCode.INR,
                result.get().getCurrency()
        );

        assertEquals(
                TransactionType.PAYMENT,
                result.get().getType()
        );
    }

    /**
     * Tests the custom repository method findByCustomerId().
     *
     * Two transactions -> CUST001 
     * one transaction -> CUST002.
     */
    @Test
    void findByCustomerId_ShouldReturnOnlyCustomersTransactions() {

        Transaction transaction1 = new Transaction(
                "TXN-REPO-002",
                "CUST001",
                new BigDecimal("100.00"),
                CurrencyCode.INR,
                TransactionType.PAYMENT
        );

        Transaction transaction2 = new Transaction(
                "TXN-REPO-003",
                "CUST001",
                new BigDecimal("200.00"),
                CurrencyCode.INR,
                TransactionType.REFUND
        );

        Transaction transaction3 = new Transaction(
                "TXN-REPO-004",
                "CUST002",
                new BigDecimal("300.00"),
                CurrencyCode.USD,
                TransactionType.PAYMENT
        );

        /*
         * Save all three transactions.
         */
        repository.save(transaction1);
        repository.save(transaction2);
        repository.save(transaction3);

        /*
         * Search for transactions belonging to CUST001.
         */
        List<Transaction> transactions =
                repository.findByCustomerId("CUST001");

        /*
         * CUST001 should have exactly two transactions.
         */
        assertEquals(2, transactions.size());

        /*
         * Verify that both returned transactions belong to CUST001.
         */
        assertTrue(
                transactions.stream()
                        .allMatch(t ->
                                t.getCustomerId().equals("CUST001"))
        );
    }

    /**
     * Tests that searching for a customer with no transactions
     * returns an empty list rather than null.
     */
    @Test
    void findByCustomerId_WhenCustomerHasNoTransactions_ShouldReturnEmptyList() {

        List<Transaction> transactions =
                repository.findByCustomerId("CUSTOMER-WITH-NO-TRANSACTIONS");

        /*
         * Spring Data JPA repository methods returning List normally
         * return an empty list when there are no matching records.
         */
        assertNotNull(transactions);
        assertTrue(transactions.isEmpty());
    }

    /**
     * Tests that findById() returns an empty Optional when the
     * requested transaction does not exist.
     */
    @Test
    void findById_WhenTransactionDoesNotExist_ShouldReturnEmptyOptional() {

        Optional<Transaction> result =
                repository.findById("DOES-NOT-EXIST");

        /*
         * No transaction should be found.
         */
        assertTrue(result.isEmpty());
    }
}