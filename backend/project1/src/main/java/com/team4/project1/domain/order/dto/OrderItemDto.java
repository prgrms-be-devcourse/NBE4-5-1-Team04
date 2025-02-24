package com.team4.project1.domain.order.dto;

import com.team4.project1.domain.order.entity.OrderItem;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderItemDto {

    private Long itemId; // 아이템 ID
    private Integer quantity; // 아이템 수량


    public OrderItemDto(Long itemId, Integer quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }


    public static OrderItemDto from(OrderItem orderItem) {
        return new OrderItemDto(
                orderItem.getItem().getId(),
                orderItem.getQuantity());
    }


    public static OrderItemDto of(Long itemId, Integer quantity) {
        return new OrderItemDto(itemId, quantity);
    }
}
