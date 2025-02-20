package com.team4.project1.domain.order.entity;

import com.team4.project1.domain.item.entity.Item;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "order_item") // 테이블 이름 명시
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 주문과 다대일 관계
    @JoinColumn(name = "order_id") // 외래키 지정
    private Order order;

    @OneToOne(fetch = FetchType.LAZY) // 상품과 일대일 관계
    @JoinColumn(name = "item_id") // 외래키 지정
    private Item item;

    @Column(nullable = false)
    private Integer quantity;
}
