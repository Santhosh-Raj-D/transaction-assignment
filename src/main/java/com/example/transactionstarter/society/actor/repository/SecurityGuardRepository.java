package com.example.transactionstarter.society.actor.repository;

import com.example.transactionstarter.society.actor.domain.SecurityGuard;
import org.springframework.data.jpa.repository.JpaRepository;

/** Basic persistence operations for {@link SecurityGuard}. */
public interface SecurityGuardRepository extends JpaRepository<SecurityGuard, String> {
}
