package com.team4.project1.domain.order.dto;

import com.team4.project1.domain.order.entity.OrderItem;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderItemDto {

    private Integer itemId;
    private Integer quantity;

    public OrderItemDto(Integer itemId, Integer quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public static OrderItemDto from(OrderItem orderItem) {
        return new OrderItemDto(
                orderItem.getItem().getId(),
                orderItem.getQuantity());
    }
}
