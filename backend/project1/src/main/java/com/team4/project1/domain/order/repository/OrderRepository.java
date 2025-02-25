package com.team4.project1.domain.order.repository;

import com.team4.project1.domain.order.entity.Order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 주문 데이터를 관리하는 JPA 리포지토리 인터페이스입니다.
 * 주문({@link Order}) 엔티티와 데이터베이스 간의 상호작용을 담당합니다.
 * Spring Data JPA의 {@link JpaRepository}를 상속받아 기본적인 CRUD 기능을 제공합니다.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    /**
     * 특정 고객의 모든 주문을 조회합니다.
     * 고객 ID를 기반으로 해당 고객이 주문한 모든 주문 목록을 가져옵니다.
     * @param customerId 조회할 고객의 ID
     * @return 해당 고객이 주문한 모든 주문 목록을 반환합니다.
     */
    Page<Order> findAllByCustomerId(Long customerId, Pageable pageable);
}
