package com.team4.project1.domain.customers.repository;

import com.team4.project1.domain.customers.entity.Customers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomersRepository extends JpaRepository<Customers, Long> {
}
