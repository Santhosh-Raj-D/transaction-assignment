package com.example.transactionstarter.society.billing.domain;

import com.example.transactionstarter.idgeneration.service.IdGenerator;
import com.example.transactionstarter.transaction.domain.CurrencyCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * A charge raised against a resident: maintenance, a utility bill, or a
 * one-time due (party hall, guest pass, penalty, etc.). Covers all three
 * via {@link PaymentHead} rather than three separate entities, to keep the
 * model simple.
 *
 * When paid, the {@code transactionId} field links to the actual money
 * movement record created in the existing transaction package.
 */
@Entity
@Table(name = "bills")
public class Bill {

    @Id
    @Column(updatable = false, nullable = false)
    private String id;

    @Column(nullable = false)
    private String residentId;

    /** ID of the SocietyAdmin/PropertyManager/Merchant who raised this bill. */
    @Column(nullable = false)
    private String raisedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentHead head;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private CurrencyCode currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BillStatus status;

    /** Set once the bill is paid; links to the Transaction record. */
    @Column
    private String transactionId;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected Bill() {
        // required by JPA
    }

    public Bill(String residentId, String raisedBy, PaymentHead head, String description, BigDecimal amount, CurrencyCode currency) {
        this.id = IdGenerator.generateBillId(residentId, head.name(), description);
        this.residentId = residentId;
        this.raisedBy = raisedBy;
        this.head = head;
        this.description = description;
        this.amount = amount;
        this.currency = currency;
        this.status = BillStatus.PENDING;
        this.createdAt = Instant.now();
    }

    public void markPaid(String transactionId) {
        this.status = BillStatus.PAID;
        this.transactionId = transactionId;
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
