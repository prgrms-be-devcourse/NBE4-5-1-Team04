package com.team4.project1.domain.order.entity;

import com.team4.project1.domain.customer.entity.Customer;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "order_tb") // 테이블 이름 설정
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE) // ID 값은 변경 불가능하도록 설정
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 고객과 다대일 관계
    @JoinColumn(name = "customer_id") // 외래키 설정
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true) // 주문 아이템과 일대다 관계
    private List<OrderItem> orderItems = new ArrayList<>();

    @Column(nullable = false)
    private Date orderDate; // 주문 날짜

    @Column(nullable = false)
    private Long totalPrice; // 총 가격

    // 주문 생성자 (편의 생성자)
    public Order(Customer customer, Date orderDate, Long totalPrice) {
        this.customer = customer;
        this.orderDate = orderDate;
        this.totalPrice = totalPrice;
    }
}
