package com.example.transactionstarter.clientflow.service;

import com.example.transactionstarter.clientflow.dto.InitiatePaymentRequest;
import com.example.transactionstarter.clientflow.dto.InitiatePaymentResponse;
import com.example.transactionstarter.idgeneration.service.IdGenerator;
import com.example.transactionstarter.society.actor.domain.Resident;
import com.example.transactionstarter.society.actor.dto.CreateResidentRequest;
import com.example.transactionstarter.society.actor.service.ActorService;
import com.example.transactionstarter.transaction.domain.Transaction;
import com.example.transactionstarter.transaction.dto.CreateTransactionRequest;
import com.example.transactionstarter.transaction.service.TransactionService;
import org.springframework.stereotype.Service;

/**
 * Orchestrates the "no manual IDs" client flow:
 *
 *   payer details -&gt; Resident created (customerId generated)
 *                  -&gt; transactionId generated
 *                  -&gt; existing TransactionService.create() reused, unchanged
 *
 * This class owns no persistence of its own - it only coordinates the
 * existing {@link ActorService} and {@link TransactionService}, plus the
 * standalone {@link IdGenerator}, so none of their business logic is
 * duplicated here.
 */
@Service
public class ClientTransactionService {

    private final ActorService actorService;
    private final TransactionService transactionService;

    public ClientTransactionService(ActorService actorService, TransactionService transactionService) {
        this.actorService = actorService;
        this.transactionService = transactionService;
    }

    public InitiatePaymentResponse initiatePayment(InitiatePaymentRequest request) {
        Resident payer = actorService.createResident(
                new CreateResidentRequest(request.getPayerName(), request.getPayerFlatId(), request.getPayerContact()));

        String transactionId = IdGenerator.generateTransactionId(
                payer.getId(), request.getReceiverId(), request.getType().name());

        CreateTransactionRequest transactionRequest = new CreateTransactionRequest(
                transactionId, payer.getId(), request.getAmount(), request.getCurrency(), request.getType());
        Transaction transaction = transactionService.create(transactionRequest);

        return new InitiatePaymentResponse(payer.getId(), transaction.getId(), transaction.getStatus(),
                transaction.getAmount(), transaction.getCurrency());
    }
}
