package com.team4.project1.domain.order.dto;

import com.team4.project1.domain.order.entity.Order;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderWithOrderItemsDto {
    private Long id;
    private Date date;
    private Long totalPrice;
    private List<OrderItemDto> orderItems;

    public static OrderWithOrderItemsDto from(Order order) {
        return OrderWithOrderItemsDto.builder()
                .id(order.getId())
                .date(order.getOrderDate())
                .totalPrice(order.getTotalPrice())
                .orderItems(order.getOrderItems().stream()
                        .map(OrderItemDto::from)
                        .collect(Collectors.toList()))
                .build();
    }
}
