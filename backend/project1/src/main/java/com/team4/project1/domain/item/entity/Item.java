package com.team4.project1.domain.item.entity;

import com.team4.project1.domain.item.dto.ItemDto;
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
    private Integer stock;

    public Item(String name, Integer price) {
        this.name = name;
        this.price = price;
    }

    public static Item fromDto(ItemDto itemDto) {
        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getPrice(), itemDto.getStock());
    }

}
