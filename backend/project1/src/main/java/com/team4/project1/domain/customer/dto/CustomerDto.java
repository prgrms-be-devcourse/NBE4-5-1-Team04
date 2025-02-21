package com.team4.project1.domain.customer.dto;

import com.team4.project1.domain.customer.entity.Customer;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CustomerDto {

    private Long id;

    @NotBlank(message = "username은 필수 입력 값입니다.")
    private String username;

    @NotBlank(message = "password는 필수 입력 값입니다.")
    private String password;

    @NotBlank(message = "name은 필수 입력 값입니다.")
    private String name;

    @NotBlank(message = "email은 필수 입력 값입니다.")
    private String email;

    public CustomerDto(Customer customer) {
        this.id = customer.getId();
        this.name = customer.getName();
        this.email = customer.getEmail();
    }
}
