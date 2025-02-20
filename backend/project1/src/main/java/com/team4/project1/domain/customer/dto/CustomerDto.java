package com.team4.project1.domain.customer.dto;

import com.team4.project1.domain.customer.entity.Customer;
import lombok.Getter;

@Getter
public class CustomerDto {
    private Long id;
    private String name;
    private String email;

    public CustomerDto(Customer customer) {
        this.id = customer.getId();
        this.name = customer.getName();
        this.email = customer.getEmail();
    }
}
