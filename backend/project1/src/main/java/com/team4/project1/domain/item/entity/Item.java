package com.team4.project1.domain.item.entity;

import com.team4.project1.domain.item.dto.ItemDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 기본 생성자, 외부 사용 제한
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE) // PK는 절대 변경 불가
    private Long id;

    @Setter
    @Column(nullable = false)
    private String name;

    @Setter
    @Column(nullable = false)
    private Integer price;

    @Setter
    @Column(nullable = false)
    private Integer stock;

    @Setter
    private UUID imageUuid = null;

    public Item(String name, Integer price, Integer stock, UUID imageUuid) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.imageUuid = imageUuid;
    }

    public Item(String name, Integer price, Integer stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    @Builder
    public Item(Long id, String name, Integer price, Integer stock, UUID imageUuid) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.imageUuid = imageUuid;
    }

    public String getImageUuidAsUri() {
        return this.imageUuid != null ? this.imageUuid + ".jpg" : "";
    }
}
