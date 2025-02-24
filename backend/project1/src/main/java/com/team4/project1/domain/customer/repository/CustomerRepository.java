package com.team4.project1.domain.customer.repository;

import com.team4.project1.domain.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 고객 엔티티에 대한 CRUD 작업을 처리하는 리포지토리 인터페이스입니다.
 * Spring Data JPA의 JpaRepository를 상속하여 기본적인 CRUD 기능을 제공합니다.
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    /**
     * 사용자 이름을 기준으로 고객을 조회합니다.
     */
    Optional<Customer> findByUsername(String username);

    /**
     * API 키를 기준으로 고객을 조회합니다.
     */
    Optional<Customer> findByApiKey(String apiKey);
}
