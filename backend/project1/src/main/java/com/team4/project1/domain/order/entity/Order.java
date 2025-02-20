package com.team4.project1.domain.order.entity;

import com.team4.project1.domain.customer.entity.Customer;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "OrderTbl")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    public Order(Customer customer, Date date, Long totalPrice) {
        this.customer = customer;
        this.date = date;
        this.totalPrice = totalPrice;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    private Customer customer;

    @Column(nullable = false)
    private Date date;

    @Column(nullable = false)
    private Long totalPrice;
}