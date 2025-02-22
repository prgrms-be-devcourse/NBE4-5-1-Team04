package com.team4.project1.domain.item.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    @NotNull
    private Integer stock;

    public Item(String name, Integer price) {
        this.name = name;
        this.price = price;
    }

    //  재고 감소 메소드
    public void reduceStock(int quantity) {
        if (stock < quantity) {
            throw new IllegalArgumentException("재고가 부족합니다. 현재 재고: " + stock);
        }
        stock -= quantity;
    }

}
