package com.team4.project1.domain.customer.entity;

import com.team4.project1.domain.order.entity.Order;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE) // id 수정 불가능
    private Long id;

    @Column(length = 100, unique = true,nullable = false)
    private String username;

    @Column(length = 100,nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @OneToMany(mappedBy = "customer",fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

    public Customer(String username, String password, String name, String email) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
    }

}
