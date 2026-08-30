package com.example.transactionstarter.society.billing.repository;

import com.example.transactionstarter.society.billing.domain.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Persistence operations for {@link Bill}. */
public interface BillRepository extends JpaRepository<Bill, String> {

    List<Bill> findByResidentId(String residentId);
}
