package com.team4.project1.domain.order.entity;

import com.team4.project1.domain.customer.entity.Customer;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 기본 생성자, 외부 사용 제한
@Table(name = "order_tbl") // 테이블 이름 설정
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE) // ID 값 변경 불가능
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Customer customer;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order", orphanRemoval = true, cascade = CascadeType.REMOVE)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private Long totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus deliveryStatus = DeliveryStatus.PROCESSING;

    // PK(id)를 제외하고 필요한 필드만 Builder로 주입받도록 변경
    @Builder
    public Order(Customer customer, LocalDateTime date, Long totalPrice, DeliveryStatus deliveryStatus) {
        this.customer = customer;
        this.date = date;
        this.totalPrice = totalPrice;
        this.deliveryStatus = deliveryStatus != null ? deliveryStatus : DeliveryStatus.PROCESSING;
    }

    // 주문 정보 변경 메서드 추가

    public void updateOrder(Long totalPrice, DeliveryStatus deliveryStatus) {
        this.totalPrice = totalPrice;
        this.deliveryStatus = deliveryStatus;
    }
}
