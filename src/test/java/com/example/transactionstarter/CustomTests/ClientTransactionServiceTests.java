package com.example.transactionstarter.CustomTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.transactionstarter.clientflow.dto.InitiatePaymentRequest;
import com.example.transactionstarter.clientflow.dto.InitiatePaymentResponse;
import com.example.transactionstarter.clientflow.service.ClientTransactionService;
import com.example.transactionstarter.society.actor.domain.Resident;
import com.example.transactionstarter.society.actor.dto.CreateResidentRequest;
import com.example.transactionstarter.society.actor.service.ActorService;
import com.example.transactionstarter.transaction.domain.CurrencyCode;
import com.example.transactionstarter.transaction.domain.Transaction;
import com.example.transactionstarter.transaction.domain.TransactionType;
import com.example.transactionstarter.transaction.dto.CreateTransactionRequest;
import com.example.transactionstarter.transaction.service.TransactionService;

/**
 * Verifies the clientflow orchestrator: a client pays without supplying
 * customerId or transactionId, and the existing ActorService/
 * TransactionService are reused (not reimplemented) to do the real work.
 */
@ExtendWith(MockitoExtension.class)
class ClientTransactionServiceTests {

    @Mock
    private ActorService actorService;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private ClientTransactionService clientTransactionService;

    @Test
    void initiatePayment_generatesIdsAndReusesExistingTransactionService() {
        Resident payer = new Resident("Asha Rao", "A-101", "9999999999");
        when(actorService.createResident(any(CreateResidentRequest.class))).thenReturn(payer);

        Transaction createdTransaction = new Transaction(
                "TXN-GENERATED", payer.getId(), new BigDecimal("500.00"), CurrencyCode.INR, TransactionType.PAYMENT);
        when(transactionService.create(any(CreateTransactionRequest.class))).thenReturn(createdTransaction);

        InitiatePaymentRequest request = new InitiatePaymentRequest(
                "Asha Rao", "A-101", "9999999999", "MER-CLUB-1",
                new BigDecimal("500.00"), CurrencyCode.INR, TransactionType.PAYMENT);

        InitiatePaymentResponse response = clientTransactionService.initiatePayment(request);

        assertNotNull(response.getCustomerId());
        assertNotNull(response.getTransactionId());
        assertEquals(payer.getId(), response.getCustomerId());

        // The orchestrator must call the existing TransactionService with a
        // transactionId it generated, not one left blank/null.
        ArgumentCaptor<CreateTransactionRequest> captor = ArgumentCaptor.forClass(CreateTransactionRequest.class);
        verify(transactionService).create(captor.capture());
        assertNotNull(captor.getValue().getTransactionId());
        assertEquals(payer.getId(), captor.getValue().getCustomerId());
    }
}
