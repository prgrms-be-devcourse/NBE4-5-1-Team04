package com.team4.project1.domain.customer.repository;

import com.team4.project1.domain.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Optional;

/**
 * 고객 관련 데이터베이스 작업을 처리하는 리포지토리 인터페이스 입니다.
 * {@link JpaRepository}를 확장하여 기본적인 CRUD 연산을 지원하며,
 * 고객의 사용자 이름과 API 키로 고객을 조회하는 메서드를 추가로 제공합니다.
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {
  
    /**
     * 사용자 이름을 기반으로 고객을 조회하는 메서드입니다.
     * @param username 조회할 고객의 사용자 이름
     * @return 주어진 사용자 이름을 가진 고객이 존재한다면
     * {@link Optional}로 감싸 반환하고,
     * 존재하지 않으면 {@link Optional#empty()} 반환합니다.
     */
    Optional<Customer> findByUsername(String username);

    /**
     * API 키를 기반으로 고객을 조회하는 메서드입니다.
     * @param apiKey 조회할 고객의 API 키
     * @return 주어진 API 키를 가진 고객이 존재한다면 {@link Optional}로 감싸 반환하고,
     * 존재하지 않으면 {@link Optional#empty()} 반환합니다.
     */
    Optional<Customer> findByApiKey(String apiKey);
}
