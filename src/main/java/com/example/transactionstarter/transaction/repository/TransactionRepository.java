package com.example.transactionstarter.transaction.repository;

import com.example.transactionstarter.transaction.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


/**
 * 
 * TransactionRepository
 * -> provides operations for Transaction records.
 * 
 * save a transaction 
 * find a transaction 
 * check whether a transaction exists 
 * find transactions belonging to a particular customer
 */
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    List<Transaction> findByCustomerId(String customerId);
}
