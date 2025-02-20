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
@Table(name = "order_tb") // 테이블 이름 명시
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 고객과 다대일 관계
    @JoinColumn(name = "customer_id") // 외래키 설정
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true) // 올바른 설정
    private List<OrderItem> orderItems = new ArrayList<>();

    @Column(nullable = false)
    private Date orderDate;

    @Column(nullable = false)
    private Long totalPrice;
}