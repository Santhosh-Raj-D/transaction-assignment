package com.example.transactionstarter.society.billing.dto;

import com.example.transactionstarter.transaction.domain.CurrencyCode;
import com.example.transactionstarter.transaction.domain.TransactionStatus;

import java.math.BigDecimal;
import java.time.Instant;

/** Receipt for a PAID bill: bill details plus the linked transaction's outcome. */
public class BillReceiptResponse {

    private final String billId;
    private final String residentId;
    private final String description;
    private final BigDecimal amount;
    private final CurrencyCode currency;
    private final String transactionId;
    private final TransactionStatus transactionStatus;
    private final Instant paidAt;

    public BillReceiptResponse(String billId, String residentId, String description, BigDecimal amount,
                                CurrencyCode currency, String transactionId, TransactionStatus transactionStatus,
                                Instant paidAt) {
        this.billId = billId;
        this.residentId = residentId;
        this.description = description;
        this.amount = amount;
        this.currency = currency;
        this.transactionId = transactionId;
        this.transactionStatus = transactionStatus;
        this.paidAt = paidAt;
    }

    public String getBillId() {
        return billId;
    }

    public String getResidentId() {
        return residentId;
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

    public String getTransactionId() {
        return transactionId;
    }

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public Instant getPaidAt() {
        return paidAt;
    }
}
