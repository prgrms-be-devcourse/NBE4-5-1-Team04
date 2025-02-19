package com.team4.project1.domain.customer.dto;

import com.team4.project1.domain.customer.entity.Customer;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class CustomerDto {
    @NonNull
    private Long id;

    @NonNull
    private String name;

    @NonNull
    private String email;

    public CustomerDto(Customer customer) {
        this.id = customer.getId();
        this.name = customer.getName();
        this.email = customer.getEmail();

    }
}
