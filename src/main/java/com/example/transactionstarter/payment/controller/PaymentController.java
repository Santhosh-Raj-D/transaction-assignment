package com.example.transactionstarter.payment.controller;

import com.example.transactionstarter.payment.dto.CreatePaymentRequest;
import com.example.transactionstarter.payment.dto.PaymentOrderResponse;
import com.example.transactionstarter.payment.dto.PaymentResponse;
import com.example.transactionstarter.payment.dto.PaymentTransactionResponse;
import com.example.transactionstarter.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // 1. Create and process a payment (order creation + strategy processing + transaction, in one call)
    // POST /api/payments
    @PostMapping
    public ResponseEntity<PaymentResponse> create(@Valid @RequestBody CreatePaymentRequest request) {
        PaymentResponse response = paymentService.createAndProcessPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 2. Get a payment order
    // GET /api/payments/{id}
    @GetMapping("/{id}")
    public PaymentOrderResponse get(@PathVariable String id) {
        return PaymentOrderResponse.from(paymentService.getOrderById(id));
    }

    // 3. Get the payment transaction(s) resulting from a payment order
    // GET /api/payments/{id}/transaction
    @GetMapping("/{id}/transaction")
    public List<PaymentTransactionResponse> getTransactions(@PathVariable String id) {
        return paymentService.getTransactionsForOrder(id).stream()
                .map(PaymentTransactionResponse::from)
                .toList();
    }
}
