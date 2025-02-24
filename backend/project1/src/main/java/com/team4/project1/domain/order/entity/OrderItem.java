package com.team4.project1.domain.order.entity;

import com.team4.project1.domain.item.entity.Item;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 기본 생성자, 외부 사용 제한
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE) // ID는 변경 불가
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 주문과 다대일 관계
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    private Item item;

    @Column(nullable = false)
    private Integer quantity;

    // PK(id)를 제외하고 필요한 필드만 Builder로 주입받도록 변경
    @Builder
    public OrderItem(Order order, Item item, Integer quantity) {
        this.order = order;
        this.item = item;
        this.quantity = quantity;
    }

    // 수량 변경 메서드 추가
    public void updateQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
