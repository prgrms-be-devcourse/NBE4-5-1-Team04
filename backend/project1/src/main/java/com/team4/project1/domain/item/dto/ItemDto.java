package com.team4.project1.domain.item.dto;

import com.team4.project1.domain.item.entity.Item;
import lombok.Getter;

@Getter
public class ItemDto {
    private Integer id;
    private String name;
    private Integer price;

    public ItemDto(Item item) {
        this.id = item.getId();
        this.name = item.getName();
        this.price = item.getPrice();
    }

    public static ItemDto from(Item item) {
        return new ItemDto(item);
    }


}