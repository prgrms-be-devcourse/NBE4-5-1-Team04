package com.team4.project1.domain.customer.entity;

import com.team4.project1.domain.order.entity.Order;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 고객의 정보를 저장하는 엔티티 클래스입니다.
 * 아이디, 사용자 이름, 비밀번호, apikey, 고객 이름, 이메일을 담고 있습니다.
 * 고객은 여러 개의 주문을 가질 수 있습니다.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Customer {

    /**
     * 고객의 고유 ID
     * 데이터베이스에서 자동으로 증가하는 값을 할당합니다.
     * id는 객체 생성 시에만 값이 설정되고 이후에는 변경이 불가능합니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE) // id는 절대 변경 불가
    private Long id;

    /**
     * 고객의 사용자 이름입니다.
     * 길이는 최대 100자 제한이있고, 비어 있을 수 없으며 반드시 입력해야합니다.
     */
    @Column(length = 100, unique = true,nullable = false)
    private String username;

    /**
     * 고객의 비밀번호입니다.
     * 고객의 로그인 비밀번호를 나타내며, 비밀번호는 비어있을수 없습니다.
     * 또한 길이는 100자 제한입니다.
     */
    @Column(length = 100,nullable = false)
    private String password;

    /**
     * 고객의 API 키 입니다
     * 고객을 식별하기 위한 API이며 유일해야합니다.
     * API키는 최대 100자까지 입력 가능합니다.
     */
    @Column(length = 22)
    private String apiKey;

    /**
     * 고객의 이름입니다.
     * 고객의 이름은 필수 입력이고, 비어 있을 수 없습니다.
     */
    @Column(nullable = false)
    private String name;

    /**
     * 고객의 이메일 주소입니다.
     * 이메일은 필수 입력 항목이며, 유일해야 하며, 비어 있을 수 없습니다.
     */
    @Column(nullable = false, unique = true)
    private String email;
    /**
     * 고객의 주문 목록입니다.
     * 고객은 여러 개의 주문을 할 수 있습니다.
     * 고객과 주문 간의 1:N의 관계를 매핑합니다.
     * 주문 목록이 필요한 시점에만 로딩되도록 지연방식을 사용하고 있습니다.
     */
    @OneToMany(mappedBy = "customer",fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

    public Customer(String username, String password, String name, String email) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    @Builder
    public Customer(Long id, String username, String password, String name, String email, String apiKey) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.apiKey = apiKey;
    }

    /**
     * 고객이 관리자인가를 확인하는 메서드
     * username이 admin인지 확인하고
     * @return관리자면 true, 아니면 false를 반환합니다.
     */
    public boolean isAdmin() {
        return username.equals("admin");
    }
}
