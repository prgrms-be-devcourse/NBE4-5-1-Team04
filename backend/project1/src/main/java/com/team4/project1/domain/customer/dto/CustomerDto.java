package com.team4.project1.domain.customer.dto;

import com.team4.project1.domain.customer.entity.Customer;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 고객 정보를 담는 데이터 전송 객체(DTO)입니다.
 * 아이디, 사용자 이름, 비밀번호, 이름, 이메일 )을 담고있습니다.
 */
 @Getter
@AllArgsConstructor
 @NoArgsConstructor
public class CustomerDto {
    /**
     * 고객의 고유 ID
     */
    private Long id;
    /**
     * 고객의 사용자 이름
     * 필수 입력 값입니다. 비어 있을 수 없습니다.
     */
    @NotBlank(message = "username은 필수 입력 값입니다.")
    private String username;
    /**
     * 고객의 비밀번호
     * 필수 입력 값입니다. 비어 있을 수 없습니다.
     */
    @NotBlank(message = "password는 필수 입력 값입니다.")
    private String password;

    /**
     * 고객의 이름
     * 필수 입력값입니다. 비어 있을 수 없습니다.
     */
    @NotBlank(message = "name은 필수 입력 값입니다.")
    private String name;

    /**
     * 고객의 이메일 주소
     * 필수 입력값입니다. 비어 있을 수 없습니다.
     */
    @NotBlank(message = "email은 필수 입력 값입니다.")
    private String email;

    /**
     * 고객의 이름과 이메일을 사용해서 {@Link CustomerDto} 객체를 생성합니다.
     *
     * @param name 고객의 이름
     * @param email 고객의 이메일
     */
 public CustomerDto(String name, String email) {
        this.name = name;
        this.email = email;
    }

    /**
     * {@Link Customer} 엔티티 객체를 {@Link CustomerDto} 객체로 변환합니다.
     * @param customer 변환할 {@Link customer} 엔티티 객체
     * @return 변환된 {@Link CustomerDto} 객체를 반환합니다.
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
     *  고객의 ID, 사용자 이름, 비밀번호, 이름, 이메일을 사용해
     *  {@Link CustomerDto} 객체를 생성합니다.
     * @param id 고객의 ID
     * @param username 고객의 사용자 이름
     * @param password 고객의 비밀번호
     * @param name 고객의 이름
     * @param email 고객의 이메일
     * @return 생성된 {@Link CustomerDto} 객체를 반환합니다.
     */
 public static CustomerDto of(Long id, String username, String password, String name, String email) {
        return new CustomerDto(id, username, password, name, email);
    }
}
