package com.example.transactionstarter.society.actor.repository;

import com.example.transactionstarter.society.actor.domain.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

/** Basic persistence operations for {@link Merchant}. */
public interface MerchantRepository extends JpaRepository<Merchant, String> {
}
