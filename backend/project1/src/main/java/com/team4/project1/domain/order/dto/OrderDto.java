package com.team4.project1.domain.order.dto;

import com.team4.project1.domain.customer.dto.CustomerDto;
import lombok.Getter;

import java.util.Date;

@Getter
public class OrderDto {
    private Long id;
    private Date date;
    private Long totalPrice;
}
