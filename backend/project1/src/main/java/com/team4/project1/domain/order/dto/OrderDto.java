package com.team4.project1.domain.order.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OrderDto {
    private Long id;
    private LocalDateTime date;
    private Long totalPrice;
}
