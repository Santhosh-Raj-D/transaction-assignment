package com.example.transactionstarter.transaction.doamin;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


/**
 * 
 * Transaction Class represents One transaction with JPA Entity.
 * contains 8 feilds
 */

/**
 * @Entity tells JPA/Hibernate:
 * This Java class represents data that should be stored in the db.
 */
@Entity 

// This tells JPA the database table name.
@Table(name = "transactions")
public class Transaction {

    //This is the primary key.
    @Id
    /**
     * id cannot be NULL
     * Once transaction is created,its ID should not change.
     */
    @Column(updatable = false, nullable = false)
    private String id;

    /**
     * This identifies which customer owns the transaction.
     * A transaction must have a customer.
     */
    @Column(nullable = false)
    private String customerId;

    /**
     * This stores the transaction amount.
     * BigDecimal is preferred over double/float.
     * Because Of precise decimal arithmetic.
     * precision = 19 → total digits
     * scale = 4      → digits after decimal
     */
    @Column(nullable = false,precision = 19,scale = 4)
    private BigDecimal amount;

    /**
     * This stores the currency of the transaction.
     * This is an enum, not a normal String.
     * @Enumerated(EnumType.STRING) Store the enum as its name/string.
     * 
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 3)
    private CurrencyCode currency;

    /**
     * Transaction
     │
     └── type
           │
           ├── DEBIT
           └── CREDIT
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 20)
    private TransactionType type;

    /**
     * This represents the current state of the transaction.
     * Transaction
     │
     └── status
           │
           ├── PENDING
           ├── SUCCESS
           └── FAILED
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)    
    private TransactionStatus status;

    /**
     * Instant represents a specific point in time.
     * This records: When was the transaction created?
     * The creation time should never change.
     */
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * This records: When was this transaction last modified?
     */
    @Column(nullable = false)
    private Instant updatedAt;

    /**
     * JPA/Hibernate needs a no-argument constructor to create entity
     * objects when reading data from the database.
     */
    protected Transaction() {
    // required by JPA
    }

    /**
     * This is the constructor your application uses when creating
     * a new transaction.
     */
    public Transaction(String id, String customerId, BigDecimal amount, CurrencyCode currency, TransactionType type) {
        this.id = id;
        this.customerId = customerId;
        this.amount = amount;
        this.currency = currency;
        this.type = type;
        /**
         * This is an important business rule.
         * It says:Every newly created transaction starts in PENDING
         * status.
         * 
         * NEW TRANSACTION
                ↓
            PENDING
                ↓
            Processing
                ↓
            SUCCESS / FAILED
         */
        this.status = TransactionStatus.PENDING;
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    /**
     * Getters
     * These allow other parts of the application to read the entity's
     * data.
     */
    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public CurrencyCode getCurrency() {
        return currency;
    }

    public TransactionType getType() {
        return type;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
        this.updatedAt = Instant.now();
    }
}
