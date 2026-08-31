package com.example.transactionstarter.payment.repository;

import com.example.transactionstarter.payment.domain.BankAccount;
import com.example.transactionstarter.payment.domain.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount, String> {

    Optional<BankAccount> findByBankIdAndMethodAndAccountRef(String bankId, PaymentMethod method, String accountRef);
}
