package com.team4.project1.domain.order.dto;

import com.team4.project1.domain.order.entity.Order;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OrderDto {
    private Long id;
    private LocalDateTime date;
    private Long totalPrice;

    public OrderDto(Long id, LocalDateTime date, Long totalPrice) {
        this.id = id;
        this.date = date;
        this.totalPrice = totalPrice;
    }

    public static OrderDto from(Order order) {
        return new OrderDto(
                order.getId(),
                order.getDate(),
                order.getTotalPrice());

    }
}
