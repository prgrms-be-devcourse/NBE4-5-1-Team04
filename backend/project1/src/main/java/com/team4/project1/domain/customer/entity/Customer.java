package com.team4.project1.domain.customer.entity;

import com.team4.project1.domain.order.entity.Order;
import jakarta.persistence.*;
import lombok.*;

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
    private Long id;

    @Column(length = 100, unique = true)
    private String username;

    @Column(length = 100)
    private String password;

    @Column(nullable =false)
    private String name;

    @Column(nullable =false, unique=true)
    private String email;

    @OneToMany(mappedBy = "customer")
    private List<Order> orders;
}
