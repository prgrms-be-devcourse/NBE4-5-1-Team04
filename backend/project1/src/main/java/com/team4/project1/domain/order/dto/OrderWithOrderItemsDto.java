package com.team4.project1.domain.order.dto;

import com.team4.project1.domain.order.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class OrderWithOrderItemsDto {
    private Long id;
    private LocalDateTime date;
    private Long totalPrice;
    private List<OrderItemDto> orderedItems;

    public static OrderWithOrderItemsDto from(Order order) {
        return new OrderWithOrderItemsDto(
                order.getId(),
                order.getDate(),
                order.getTotalPrice(),
                order.getOrderItems().stream()
                        .map(OrderItemDto::from)
                        .toList()
        );
    }
}
