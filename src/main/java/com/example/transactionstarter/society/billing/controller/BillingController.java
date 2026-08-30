package com.example.transactionstarter.society.billing.controller;

import com.example.transactionstarter.society.billing.dto.BillResponse;
import com.example.transactionstarter.society.billing.dto.CreateBillRequest;
import com.example.transactionstarter.society.billing.service.BillingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

/** REST endpoints for raising and paying society bills. */
@RestController
@RequestMapping("/api/society")
public class BillingController {

    private final BillingService billingService;

    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    @PostMapping("/bills")
    public ResponseEntity<BillResponse> create(@Valid @RequestBody CreateBillRequest request) {
        BillResponse body = BillResponse.from(billingService.createBill(request));
        return ResponseEntity.created(URI.create("/api/society/bills/" + body.getId())).body(body);
    }

    @GetMapping("/bills/{id}")
    public BillResponse get(@PathVariable String id) {
        return BillResponse.from(billingService.getById(id));
    }

    @GetMapping("/residents/{residentId}/bills")
    public List<BillResponse> getForResident(@PathVariable String residentId) {
        return billingService.getByResidentId(residentId).stream()
                .map(BillResponse::from)
                .collect(Collectors.toList());
    }

    @PostMapping("/bills/{id}/pay")
    public BillResponse pay(@PathVariable String id) {
        return BillResponse.from(billingService.pay(id));
    }
}
