package com.team4.project1.domain.order.dto;

import com.team4.project1.domain.order.entity.Order;
import com.team4.project1.domain.order.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@AllArgsConstructor
public class OrderWithOrderItemsDto {
    private Long id; // 주문 ID
    private LocalDateTime date; // 주문 날짜
    private Long totalPrice; // 총 가격
    private String deliveryStatus; // 배송 상태
    private List<OrderItemDto> orderedItems; // 주문 항목 목록


    public static OrderWithOrderItemsDto from(Order order) {
        return new OrderWithOrderItemsDto(
                order.getId(),
                order.getDate(),
                order.getTotalPrice(),
                order.getDeliveryStatus().name(),
                order.getOrderItems().stream()
                        .map(OrderItemDto::from)
                        .toList()
        );
    }


    public static OrderWithOrderItemsDto of(Long id, LocalDateTime date, Long totalPrice, String deliveryStatus, List<OrderItem> orderItems) {
        return new OrderWithOrderItemsDto(id, date, totalPrice, deliveryStatus, orderItems.stream().map(OrderItemDto::from).toList());
    }
}
