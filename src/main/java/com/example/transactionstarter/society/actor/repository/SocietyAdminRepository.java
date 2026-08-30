package com.example.transactionstarter.society.actor.repository;

import com.example.transactionstarter.society.actor.domain.SocietyAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

/** Basic persistence operations for {@link SocietyAdmin}. */
public interface SocietyAdminRepository extends JpaRepository<SocietyAdmin, String> {
}
