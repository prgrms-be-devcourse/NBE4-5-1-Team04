package com.team4.project1.domain.item.dto;

import com.team4.project1.domain.item.entity.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Locale;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;
    private String name;
    private Integer price;

    public static ItemDto from(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getPrice()
        );
    }

    public static ItemDto of(Long id, String name, Integer price) {
        return new ItemDto(id, name, price);
    }
}