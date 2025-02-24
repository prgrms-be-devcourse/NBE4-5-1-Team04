package com.team4.project1.domain.item.dto;

import com.team4.project1.domain.item.entity.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Locale;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;
    private String name;
    private Integer price;
    private Integer stock;
    private String imageUri;

    public static ItemDto from(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getPrice(),
                item.getStock(),
                item.getImageUuidAsUri()
        );
    }

    public static ItemDto of(Long id, String name, Integer price, Integer stock) {
        return new ItemDto(id, name, price, stock, null);
    }

    public static ItemDto of(Long id, String name, Integer stock, Integer price, String imageUri) {
        return new ItemDto(id, name, price, stock, imageUri);
    }

    public Item toEntity() {
        return new Item(
                this.id,
                this.name,
                this.price,
                this.stock,
                this.imageUri.isEmpty() ?
                        null :
                        UUID.fromString(this.imageUri.substring(0, this.imageUri.length() - ".jpg".length()))
        );
    }
}