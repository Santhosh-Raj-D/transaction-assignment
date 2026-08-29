package com.example.transactionstarter.transaction.controller;

import com.example.transactionstarter.transaction.domain.Transaction;
import com.example.transactionstarter.transaction.dto.CreateTransactionRequest;
import com.example.transactionstarter.transaction.dto.TransactionResponse;
import com.example.transactionstarter.transaction.dto.UpdateStatusRequest;
import com.example.transactionstarter.transaction.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // 1. Create transaction
    // POST /api/transactions
    @PostMapping("/transactions")
    public ResponseEntity<TransactionResponse> create(@Valid @RequestBody CreateTransactionRequest request) {
        Transaction created = transactionService.create(request);
        TransactionResponse body = TransactionResponse.from(created);
        return ResponseEntity.created(URI.create("/api/transactions/" + created.getId())).body(body);
    }

    // 2. Get transaction
    // GET /api/transactions/{id}
    @GetMapping("/transactions/{id}")
    public TransactionResponse get(@PathVariable String id) {
        return TransactionResponse.from(transactionService.getById(id));
    }

    // 3. Update transaction status
    // PATCH /api/transactions/{id}/status
    @PatchMapping("/transactions/{id}/status")
    public TransactionResponse updateStatus(@PathVariable String id, @Valid @RequestBody UpdateStatusRequest request) {
        Transaction updated = transactionService.updateStatus(id, request.getStatus());
        return TransactionResponse.from(updated);
    }

    // 4. Get all transactions for a customer
    // GET /api/customers/{customerId}/transactions
    @GetMapping("/customers/{customerId}/transactions")
    public List<TransactionResponse> getForCustomer(@PathVariable String customerId) {
        return transactionService.getByCustomerId(customerId).stream()
                .map(TransactionResponse::from)
                .collect(Collectors.toList());
    }
}
