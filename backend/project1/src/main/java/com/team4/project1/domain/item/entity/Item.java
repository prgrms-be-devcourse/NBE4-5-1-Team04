package com.team4.project1.domain.item.entity;

import com.team4.project1.domain.item.dto.ItemDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

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

    private UUID imageUuid = null;

    public Item(String name, Integer price, Integer stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public Item(String name, Integer price, Integer stock, UUID imageUuid) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.imageUuid = imageUuid;
    }

    public static Item fromDto(ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getPrice(),
                itemDto.getStock(),
                itemDto.getImageUri().isEmpty() ?
                        null :
                        UUID.fromString(itemDto.getImageUri().substring(0, itemDto.getImageUri().length() - ".jpg".length()))
        );
    }

    public String getImageUuidAsUri() {
        return this.imageUuid != null ? this.imageUuid + ".jpg" : "";
    }
}
