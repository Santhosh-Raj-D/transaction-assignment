package com.example.transactionstarter.society.billing.repository;

import com.example.transactionstarter.society.billing.domain.Bill;
import com.example.transactionstarter.society.billing.domain.BillStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Persistence operations for {@link Bill}. */
public interface BillRepository extends JpaRepository<Bill, String> {

    List<Bill> findByResidentId(String residentId);

    List<Bill> findByStatus(BillStatus status);

    List<Bill> findByResidentIdAndStatus(String residentId, BillStatus status);
}
