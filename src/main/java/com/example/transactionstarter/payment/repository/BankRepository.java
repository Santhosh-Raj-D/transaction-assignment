package com.example.transactionstarter.payment.repository;

import com.example.transactionstarter.payment.domain.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankRepository extends JpaRepository<Bank, String> {

    Optional<Bank> findByName(String name);
}
