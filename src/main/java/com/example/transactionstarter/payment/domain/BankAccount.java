package com.example.transactionstarter.payment.domain;

import com.example.transactionstarter.idgeneration.service.IdGenerator;
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

/**
 * A single payment instrument belonging to a {@link Bank}: one row per
 * (bank, method, reference) combination.
 *
 * {@code accountRef} is intentionally a generic, non-sensitive reference -
 * a UPI VPA, a masked card number, a wallet ID, or a net-banking account
 * reference - never a raw card number, CVV, PIN, or password. This backend
 * is a simulation and never stores real payment credentials.
 */
@Entity
@Table(name = "bank_accounts")
public class BankAccount {

    @Id
    @Column(updatable = false, nullable = false)
    private String id;

    @Column(name = "bank_id", nullable = false)
    private String bankId;

    /** Read-only navigable relationship to the same bank_id column; bankId remains the source of truth. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_id", referencedColumnName = "id", insertable = false, updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Bank bank;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentMethod method;

    /** Masked/demo reference only - never a raw card number, CVV, PIN, or password. */
    @Column(nullable = false)
    private String accountRef;

    protected BankAccount() {
        // required by JPA
    }

    public BankAccount(String bankId, PaymentMethod method, String accountRef) {
        this.id = IdGenerator.generateBankAccountId(bankId, accountRef);
        this.bankId = bankId;
        this.method = method;
        this.accountRef = accountRef;
    }

    public String getId() {
        return id;
    }

    public String getBankId() {
        return bankId;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public String getAccountRef() {
        return accountRef;
    }

    public Bank getBank() {
        return bank;
    }
}
