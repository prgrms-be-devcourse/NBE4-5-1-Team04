package com.team4.project1.domain.customers.dto;

import com.team4.project1.domain.customers.entity.Customers;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class CustomersDto {
    @NonNull
    private Long id;

    @NonNull
    private String name;

    @NonNull
    private String email;

    public CustomersDto(Customers customers) {
        this.id = customers.getId();
        this.name = customers.getName();
        this.email = customers.getEmail();

    }
}
