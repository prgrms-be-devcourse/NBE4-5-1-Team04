package com.team4.project1.domain.order.entity;

import com.team4.project1.domain.customer.entity.Customer;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 주문 정보를 저장하는 엔티티 클래스입니다.
 * 각 주문은 특정 고객(Customer)과 연관되며, 여러 개의 주문 항목(OrderItem)을 가질 수 있습니다.
 * 주문 날짜, 총 가격, 배송 상태 등의 정보를 포함합니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 기본 생성자, 외부 사용 제한
@Table(name = "order_tbl") // 테이블 이름 설정
public class Order {

    /**
     * 상품의 ID
     * 데이터베이스에서 자동으로 증가하는 값을 할당합니다.
     * id는 객체 생성 시에만 값이 설정되고 이후에는 변경이 불가능합니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE) // ID 값 변경 불가능
    private Long id;

    /**
     * 주문을 한 고객
     * 지연방식을 사용하고 있습니다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private Customer customer;

    /**
     * 주문에 포함된 주문 항목 목록
     * 주문은 여러 개의 주문 항목({@link OrderItem})을 포함할 수 있습니다.
     * OrderItem 엔티티에서 "order" 필드를 통해 매핑된다.
     * 주문 항목이 리스트에서 제거되면 자동으로 삭제된다.
     * 주문이 삭제되면 관련 주문 항목도 함께 삭제된다.
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order", orphanRemoval = true, cascade = CascadeType.REMOVE)
    private List<OrderItem> orderItems = new ArrayList<>();

    /**
     * 주문 날짜
     * 주문이 생성된 날짜 및 시간을 저장하며, 반드시 값이 있어야 합니다.
     */
    @Setter
    @Column(nullable = false)
    private LocalDateTime date;

    /**
     * 주문 총 가격
     * 주문에 포함된 모든 아이템의 총 가격을 저장하며, 반드시 값이 있어야 합니다.
     */
    @Setter
    @Column(nullable = false)
    private Long totalPrice;

    /**
     * 주문의 배송 상태
     * {@link OrderStatus} 열거형을 사용하여 주문의 현재 상태를 나타냅니다
     * {@code PROCESSING}: 주문이 처리 중인 상태를 의미합니다.
     * {@code SHIPPED}: 주문이 배송된 상태를 의미합니다
     */
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus = OrderStatus.TEMPORARY;

    /**
     * 새로운 주문을 생성하는 생성자입니다.
     * @param customer 주문을 한 고객
     * @param date 주문 날짜
     * @param totalPrice 총 가격
     */
    public Order(Customer customer, LocalDateTime date, Long totalPrice) {
        this.customer = customer;
        this.date = date;
        this.totalPrice = totalPrice;
    }

    @Builder
    public Order(Long id, Customer customer, LocalDateTime date, Long totalPrice, OrderStatus orderStatus) {
        this.id = id;
        this.customer = customer;
        this.date = date;
        this.totalPrice = totalPrice;
        this.orderStatus = orderStatus != null ? orderStatus : OrderStatus.TEMPORARY;
    }

    // 주문 정보 변경 메서드 추가
    public void updateOrder(Long totalPrice, OrderStatus orderStatus) {
        this.totalPrice = totalPrice;
        this.orderStatus = orderStatus;
    }
}
