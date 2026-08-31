package com.example.transactionstarter.society.billing.controller;

import com.example.transactionstarter.society.billing.dto.BillReceiptResponse;
import com.example.transactionstarter.society.billing.dto.BillResponse;
import com.example.transactionstarter.society.billing.dto.CreateBillRequest;
import com.example.transactionstarter.society.billing.domain.BillStatus;
import com.example.transactionstarter.society.billing.service.BillingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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

    @PatchMapping("/bills/{id}/cancel")
    public BillResponse cancel(@PathVariable String id) {
        return BillResponse.from(billingService.cancel(id));
    }

    @GetMapping("/residents/{residentId}/bills/outstanding")
    public List<BillResponse> outstandingForResident(@PathVariable String residentId) {
        return billingService.getOutstandingForResident(residentId).stream()
                .map(BillResponse::from)
                .collect(Collectors.toList());
    }

    /** Admin-wide filter across all residents' bills, e.g. GET /api/society/bills?status=PENDING */
    @GetMapping("/bills")
    public List<BillResponse> filterByStatus(@RequestParam String status) {
        BillStatus parsedStatus;
        try {
            parsedStatus = BillStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status value: " + status);
        }
        return billingService.getByStatus(parsedStatus).stream()
                .map(BillResponse::from)
                .collect(Collectors.toList());
    }

    @GetMapping("/bills/{id}/receipt")
    public BillReceiptResponse receipt(@PathVariable String id) {
        return billingService.getReceipt(id);
    }
}
