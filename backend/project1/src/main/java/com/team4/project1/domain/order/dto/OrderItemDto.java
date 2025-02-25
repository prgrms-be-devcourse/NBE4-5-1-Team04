package com.team4.project1.domain.order.dto;

import com.team4.project1.domain.order.entity.OrderItem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 주문항목(OrderItem) 정보를 전달하기 위한 DTO 클래스입니다.
 * 주문된 상품의 ID와 수량 정보를 포함합니다.
 */
@Setter
@Getter
@NoArgsConstructor
public class OrderItemDto {

    /** 주문된 상품의 상품ID*/
    private Long itemId; // 아이템 ID

    /** 주문된 상품의 수량*/
    private Integer quantity; // 아이템 수량

    /**
     * 주어진 상품 ID와 수량을 사용해 OrderItemDto 객체를 생성하는 생성자
     * @param itemId 주문된 상품의 ID
     * @param quantity 주문된 상품의 수량
     */
    public OrderItemDto(Long itemId, Integer quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }

    /**
     * 엔티티 객체를 DTO로 변환하는 메서드입니다.
     * {@Link OrderItem} 객체의 데이터를 사용해서 @link OrderItemDto} 인스턴스를 생성합니다.
     * @param orderItem
     * @return
     */
    public static OrderItemDto from(OrderItem orderItem) {
        return new OrderItemDto(
                orderItem.getItem().getId(),
                orderItem.getQuantity());
    }

    /**
     * 주어진 값으로 새로운 OrderItemDto 객체를 생성하는 메서드입니다.
     * @param itemId 주문된 상품의 ID
     * @param quantity 주문된 상품의 수량
     * @return 생성된 {@Link OrderItemDto} 객체를 반환합니다.
     */
    public static OrderItemDto of(Long itemId, Integer quantity) {
        return new OrderItemDto(itemId, quantity);
    }
}
