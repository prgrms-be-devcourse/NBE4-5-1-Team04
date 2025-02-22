package com.team4.project1.domain.order.dto;

import com.team4.project1.domain.order.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class OrderDto {
    private Long id;
    private LocalDateTime date;
    private Long totalPrice;

    public static OrderDto from(Order order) {
        return new OrderDto(
                order.getId(),
                order.getDate(),
                order.getTotalPrice()
        );
    }

    public static OrderDto of(Long id, LocalDateTime date, Long totalPrice) {
        return new OrderDto(id, date, totalPrice);
    }
}
