package com.team4.project1.domain.order.repository;

import com.team4.project1.domain.order.entity.Order;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findAllByCustomerId(Long customerId);

}
