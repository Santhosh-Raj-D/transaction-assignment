package com.example.transactionstarter.payment.repository;

import com.example.transactionstarter.payment.domain.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, String> {
}
