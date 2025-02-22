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
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 주문과 다대일 관계
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    private Item item;

    @Column(nullable = false)
    private Integer quantity;


    public OrderItem(Order order, Item item, Integer quantity) {
        this.order = order;
        this.item = item;
        this.quantity = quantity;
    }
}
