package com.team4.project1.domain.customer.dto;

import com.team4.project1.domain.customer.entity.Customer;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


 @Getter
@AllArgsConstructor
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

 public CustomerDto(String name, String email) {
        this.name = name;
        this.email = email;
    }

 public static CustomerDto from(Customer customer) {
        return new CustomerDto(
            customer.getId(),
            customer.getUsername(),
            customer.getPassword(),
            customer.getName(),
            customer.getEmail()
        );
    }

 public static CustomerDto of(Long id, String username, String password, String name, String email) {
        return new CustomerDto(id, username, password, name, email);
    }
}
