package com.example.transactionstarter.society.billing.dto;

import com.example.transactionstarter.society.billing.domain.Bill;
import com.example.transactionstarter.society.billing.domain.BillStatus;
import com.example.transactionstarter.society.billing.domain.PaymentHead;
import com.example.transactionstarter.transaction.domain.CurrencyCode;

import java.math.BigDecimal;
import java.time.Instant;

/** Data returned to the client for a Bill. */
public class BillResponse {

    private final String id;
    private final String residentId;
    private final String raisedBy;
    private final PaymentHead head;
    private final String description;
    private final BigDecimal amount;
    private final CurrencyCode currency;
    private final BillStatus status;
    private final String transactionId;
    private final Instant createdAt;

    public BillResponse(String id, String residentId, String raisedBy, PaymentHead head, String description,
                         BigDecimal amount, CurrencyCode currency, BillStatus status, String transactionId, Instant createdAt) {
        this.id = id;
        this.residentId = residentId;
        this.raisedBy = raisedBy;
        this.head = head;
        this.description = description;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.transactionId = transactionId;
        this.createdAt = createdAt;
    }

    public static BillResponse from(Bill bill) {
        return new BillResponse(bill.getId(), bill.getResidentId(), bill.getRaisedBy(), bill.getHead(),
                bill.getDescription(), bill.getAmount(), bill.getCurrency(), bill.getStatus(), bill.getTransactionId(), bill.getCreatedAt());
    }

    public String getId() {
        return id;
    }

    public String getResidentId() {
        return residentId;
    }

    public String getRaisedBy() {
        return raisedBy;
    }

    public PaymentHead getHead() {
        return head;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public CurrencyCode getCurrency() {
        return currency;
    }

    public BillStatus getStatus() {
        return status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
