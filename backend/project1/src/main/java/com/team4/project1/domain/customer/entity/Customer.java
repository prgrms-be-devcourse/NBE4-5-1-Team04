package com.team4.project1.domain.customer.entity;

import com.team4.project1.domain.order.entity.Order;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE) // id는 절대 변경 불가
    private Long id;

    @Column(length = 100, unique = true, nullable = false)
    private String username;

    @Column(length = 100, nullable = false)
    private String password;

    @Column(length = 100, unique = true)
    private String apiKey;

    @Setter
    @Column(nullable = false)
    private String name;

    @Setter
    @Column(nullable = false, unique = true)
    private String email;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

    // Lombok Builder에서 id를 제외하고, 필요한 필드만 생성자로 받아서 엔티티 생성.
    @Builder
    public Customer(String username, String password, String name, String email, String apiKey) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.apiKey = apiKey;
    }

//    public Customer(String username, String password, String name, String email) {
//        this.username = username;
//        this.password = password;
//        this.name = name;
//        this.email = email;
//    }
//
//    public Customer(String username, String password) {
//        this.username = username;
//        this.password = password;
//        this.apiKey = username;
//    }

    public boolean isAdmin() {
        return username.equals("admin");
    }
}
