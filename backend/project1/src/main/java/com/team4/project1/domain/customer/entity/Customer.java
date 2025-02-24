package com.team4.project1.domain.customer.entity;

import com.team4.project1.domain.order.entity.Order;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 고객 정보를 저장하는 엔티티 클래스입니다.
 * 고객은 여러 개의 주문(Order)을 가질 수 있습니다.
 */
 @Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    /**
     * 고객의 고유 ID
     * 자동 생성되는 값으로 수정 불가능합니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE) // id 수정 불가능
    private Long id;
    /**
     * 고객의 사용자 이름
     * 고유하고 필수로 입력해야하는 값입니다.
     */
    @Column(length = 100, unique = true,nullable = false)
    private String username;
    /**
     * 고객의 비밀번호
     * 필수로 입력해야하는 값입니다.
     */
    @Column(length = 100,nullable = false)
    private String password;
    /**
     * 고객의 API 키
     * 고유 값이며, 사용자 이름을 기반으로 생성될 수 있습니다.
     */
    @Column(length = 100, unique = true)
    private String apiKey;
    /**
     * 고객의 이름
     * 필수로 입력해야하는 값입니다.
     */
    @Column(nullable = false)
    private String name;
    /**
     * 고객의 이메일
     * 고유 값이며, 필수로 입력해야하는 값입니다.
     */
    @Column(nullable = false, unique = true)
    private String email;
    /**
     * 고객의 주문 목록
     * 고객은 여러 개의 주문을 가질 수 있고, 지연 로딩 방식으로 처리됩니다.
     */
    @OneToMany(mappedBy = "customer",fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();
    /**
     * 고객 객체를 생성하는 기본 생성자입니다.
     */
    public Customer(String username, String password, String name, String email) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
    }
    /**
     * 사용자 이름과 비밀번호로만 생성되는 고객 객체입니다.
     * API 키는 사용자 이름을 기반으로 생성됩니다.
     */
     public Customer(String username, String password) {
        this.username = username;
        this.password = password;
        this.apiKey = username;
    }
/**
 * 고객이 관리자(Admin)인지 확인하는 메서드입니다.
 */
 public boolean isAdmin() {
        return username.equals("admin");
    }
}
