package com.team4.project1.domain.order.dto;

import lombok.Getter;

import java.util.Date;
import java.util.List;

@Getter
public class OrderWithOrderItemsDto {
    private Long id;
    private Date date;
    private Long totalPrice;
    private List<OrderItemDto> orderItems;
}
