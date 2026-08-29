package com.example.transactionstarter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import com.example.transactionstarter.transaction.domain.CurrencyCode;
import com.example.transactionstarter.transaction.domain.Transaction;
import com.example.transactionstarter.transaction.domain.TransactionStatus;
import com.example.transactionstarter.transaction.domain.TransactionType;
import com.example.transactionstarter.transaction.repository.TransactionRepository;
import com.example.transactionstarter.transaction.service.TransactionService;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository repository;

    @InjectMocks
    private TransactionService service;

    @Test
    void updateStatusAllowsPendingToCompleted() {

        Transaction transaction = new Transaction(
                "txn-001",
                "CUST001", new BigDecimal("500.00"),
                CurrencyCode.INR,
                TransactionType.PAYMENT
        );

        when(repository.findById(transaction.getId()))
                .thenReturn(Optional.of(transaction));

        when(repository.save(transaction))
        .thenReturn(transaction);
        
        Transaction result =
                service.updateStatus(
                        transaction.getId(),
                        TransactionStatus.COMPLETED
                );

        assertEquals(
                TransactionStatus.COMPLETED,
                result.getStatus()
        );

        verify(repository).save(transaction);
    }
}