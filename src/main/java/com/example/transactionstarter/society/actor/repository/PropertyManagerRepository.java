package com.example.transactionstarter.society.actor.repository;

import com.example.transactionstarter.society.actor.domain.PropertyManager;
import org.springframework.data.jpa.repository.JpaRepository;

/** Basic persistence operations for {@link PropertyManager}. */
public interface PropertyManagerRepository extends JpaRepository<PropertyManager, String> {
}
