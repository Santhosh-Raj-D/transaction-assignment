package com.example.transactionstarter.payment.domain;

import com.example.transactionstarter.idgeneration.service.IdGenerator;
import com.example.transactionstarter.transaction.domain.CurrencyCode;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Represents the request/order to make a payment - "how is this payment
 * being asked for" - as distinct from {@link PaymentTransaction}, which
 * represents the actual attempt/result of processing this order.
 */
@Entity
@Table(name = "payment_orders")
public class PaymentOrder {

    @Id
    @Column(updatable = false, nullable = false)
    private String id;

    @Column(nullable = false)
    private String payerId;

    @Column(nullable = false)
    private String payeeId;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private CurrencyCode currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentMethod method;

    @Column(name = "sender_bank_account_id", nullable = false)
    private String senderBankAccountId;

    @Column(name = "receiver_bank_account_id", nullable = false)
    private String receiverBankAccountId;

    /** Read-only navigable relationships to the same *_bank_account_id columns above. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_bank_account_id", referencedColumnName = "id", insertable = false, updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private BankAccount senderBankAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_bank_account_id", referencedColumnName = "id", insertable = false, updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private BankAccount receiverBankAccount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    protected PaymentOrder() {
        // required by JPA
    }

    public PaymentOrder(String payerId, String payeeId, BigDecimal amount, CurrencyCode currency,
                         PaymentMethod method, String senderBankAccountId, String receiverBankAccountId) {
        this.id = IdGenerator.generatePaymentOrderId(payerId, payeeId, method.name());
        this.payerId = payerId;
        this.payeeId = payeeId;
        this.amount = amount;
        this.currency = currency;
        this.method = method;
        this.senderBankAccountId = senderBankAccountId;
        this.receiverBankAccountId = receiverBankAccountId;
        this.status = PaymentStatus.INITIATED;
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    public String getId() {
        return id;
    }

    public String getPayerId() {
        return payerId;
    }

    public String getPayeeId() {
        return payeeId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public CurrencyCode getCurrency() {
        return currency;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public String getSenderBankAccountId() {
        return senderBankAccountId;
    }

    public String getReceiverBankAccountId() {
        return receiverBankAccountId;
    }

    public BankAccount getSenderBankAccount() {
        return senderBankAccount;
    }

    public BankAccount getReceiverBankAccount() {
        return receiverBankAccount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
        this.updatedAt = Instant.now();
    }
}
