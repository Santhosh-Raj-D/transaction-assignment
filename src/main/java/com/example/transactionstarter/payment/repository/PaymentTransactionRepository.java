package com.example.transactionstarter.payment.repository;

import com.example.transactionstarter.payment.domain.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, String> {

    List<PaymentTransaction> findByPaymentOrderId(String paymentOrderId);
}
