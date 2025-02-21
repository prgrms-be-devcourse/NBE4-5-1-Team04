package com.team4.project1.domain.customer.repository;

import com.team4.project1.domain.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByUsername(String username);
    Optional<Customer> findByApiKey(String apiKey);
}
