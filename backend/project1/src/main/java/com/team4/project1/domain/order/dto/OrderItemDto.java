package com.team4.project1.domain.order.dto;

import com.team4.project1.domain.order.entity.OrderItem;
import lombok.Getter;

@Getter
public class OrderItemDto {
    // in api doc, orders are received in the form of a list of orderitems
    // each orderitem has 2 fields, the item id and quantity
    // real orderitem entity has 4 fields; id, item id, quantity, and parent order

    private Long id;
    private Integer itemId;
    private Long orderId;
    private Integer quantity;

    public OrderItemDto(Integer itemId, Integer quantity) {
        this.id = 0L;
        this.itemId = itemId;
        this.orderId = 0L;
        this.quantity = quantity;
    }

    public OrderItemDto(Long id, Integer itemId, Long orderId, Integer quantity) {
        this.id = id;
        this.itemId = itemId;
        this.orderId = orderId;
        this.quantity = quantity;
    }

    public static OrderItemDto from(OrderItem orderItem) {
        return new OrderItemDto(
                orderItem.getId(),
                orderItem.getItem().getId(),
                orderItem.getOrder().getId(),
                orderItem.getQuantity());
    }
}
