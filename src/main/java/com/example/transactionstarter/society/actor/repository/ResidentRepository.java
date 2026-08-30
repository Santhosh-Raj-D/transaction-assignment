package com.example.transactionstarter.society.actor.repository;

import com.example.transactionstarter.society.actor.domain.Resident;
import org.springframework.data.jpa.repository.JpaRepository;

/** Basic persistence operations for {@link Resident}. */
public interface ResidentRepository extends JpaRepository<Resident, String> {
}
