package com.team4.project1.domain.customer.dto;

import com.team4.project1.domain.customer.entity.Customer;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 고객 정보를 전달하기 위한 DTO(Data Transfer Object) 클래스입니다.
 * 고객의 기본 정보인 아이디, 사용자 이름, 비밀번호, 이름, 이메일을 포함합니다.
 */
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
/**
 * 이름과 이메일을 입력받아 생성하는 생성자입니다.
 */
 public CustomerDto(String name, String email) {
        this.name = name;
        this.email = email;
    }
/**
 * 고객 엔티티(Customer)를 DTO로 변환하는 메서드입니다.
 */
 public static CustomerDto from(Customer customer) {
        return new CustomerDto(
            customer.getId(),
            customer.getUsername(),
            customer.getPassword(),
            customer.getName(),
            customer.getEmail()
        );
    }
/**
 * 고객 정보를 바탕으로 DTO 객체를 생성하는 메서드입니다.
 */
 public static CustomerDto of(Long id, String username, String password, String name, String email) {
        return new CustomerDto(id, username, password, name, email);
    }
}
