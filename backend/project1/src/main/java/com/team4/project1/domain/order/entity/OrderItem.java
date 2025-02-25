package com.team4.project1.domain.order.entity;

import com.team4.project1.domain.item.entity.Item;
import jakarta.persistence.*;
import lombok.*;

/**
 * 주문 항목을 저장하는 엔티티 클래스입니다.
 * 각 주문({@link Order})에는 여러 개의 주문 항목이 포함될 수 있으며,
 * 주문 항목은 특정 상품({@link Item})과 연결됩니다.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 기본 생성자, 외부 사용 제한
public class OrderItem {

    /**
     * 주문 항목 ID
     * 데이터베이스에서 자동으로 증가하는 값을 할당합니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE) // ID는 변경 불가
    private Long id;

    /**
     * 주문 정보
     * 이 주문 항목이 속한 주문({@link Order})을 나타냅니다.
     * {@code @ManyToOne} 관계로 설정되어 있으며, 주문 항목은 하나의 주문과 연결됩니다.
     * 다대일 관계로 설정되었으며, 지연 로딩을 사용합니다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    /**
     * 상품 ID (FK)
     * 이 주문 항목이 포함하는 상품({@link Item})의 ID입니다.
     * 다대일관계로 설정되었으며, 지연 로딩을 사용합니다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private Item item;

    /**
     * 주문한 상품의 수량
     * 사용자가 특정 상품을 몇 개 주문했는지를 저장합니다.
     * 반드시 값이 있어야 합니다.
     */
    @Column(nullable = false)
    private Integer quantity;

    /**
     * 주문 항목을 생성하는 생성자입니다.
     * 주문 항목을 생성할 때, 해당 주문 정보, 상품 정보, 수량을 입력받아야 합니다.
     * @param order    이 주문 항목이 속한 주문
     * @param item     주문한 상품
     * @param quantity 주문한 상품의 수량
     */
    public OrderItem(Order order, Item item, Integer quantity) {
        this.order = order;
        this.item = item;
        this.quantity = quantity;
    }

    @Builder
    public OrderItem(Long id, Order order, Item item, Integer quantity) {
        this.id = id;
        this.order = order;
        this.item = item;
        this.quantity = quantity;
    }

    // 수량 변경 메서드 추가
    public void updateQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
