package com.example.transactionstarter.payment.domain;

import com.example.transactionstarter.idgeneration.service.IdGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * A bank/financial institution that can act as the sender or receiver
 * side of a payment via one of its {@link BankAccount}s.
 *
 * Intentionally minimal: this is a demo backend, not a real banking
 * system, so no real institution codes/routing data are modeled.
 */
@Entity
@Table(name = "banks")
public class Bank {

    @Id
    @Column(updatable = false, nullable = false)
    private String id;

    @Column(nullable = false)
    private String name;

    protected Bank() {
        // required by JPA
    }

    public Bank(String name) {
        this.id = IdGenerator.generateBankId(name);
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
