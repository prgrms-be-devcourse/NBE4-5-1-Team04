package com.team4.project1.domain.order.repository;

import com.team4.project1.domain.order.entity.Order;
import com.team4.project1.domain.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 주문 항목(OrderItem) 엔티티의 데이터 접근 레포지토리.
 * 주문 항목 관련 데이터베이스 연산을 수행한다.
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    /**
     * 특정 주문 ID에 해당하는 주문 항목 리스트를 조회하는 메서드.
     *
     * @param orderId 조회할 주문의 ID
     * @return 해당 주문에 속한 주문 항목 리스트
     */
    List<OrderItem> findByOrderId(Long orderId);

}
