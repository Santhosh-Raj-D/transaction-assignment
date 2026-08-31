package com.example.transactionstarter.transaction.repository;

import com.example.transactionstarter.transaction.domain.Transaction;
import com.example.transactionstarter.transaction.domain.TransactionStatus;
import com.example.transactionstarter.transaction.domain.TransactionType;
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
 * find transactions by status/type (added for filtering APIs)
 */
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    List<Transaction> findByCustomerId(String customerId);

    List<Transaction> findByStatus(TransactionStatus status);

    List<Transaction> findByType(TransactionType type);
}
