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

    @Builder
    // PK를 제외한 필드만 Builder를 통해 주입할 수 있도록 제한.
    public Item(String name, Integer price, Integer stock, UUID imageUuid) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.imageUuid = imageUuid;
    }

    // DTO를 기반으로 Item 객체를 생성하는 정적 팩토리 메서드
    public static Item fromDto(ItemDto itemDto) {
        return Item.builder()
                .name(itemDto.getName())
                .price(itemDto.getPrice())
                .stock(itemDto.getStock())
                .imageUuid(
                    itemDto.getImageUri().isEmpty() ?
                        null :
                        UUID.fromString(itemDto.getImageUri().substring(0, itemDto.getImageUri().length() - ".jpg".length()))
                )
                .build();
    }

    public String getImageUuidAsUri() {
        return this.imageUuid != null ? this.imageUuid + ".jpg" : "";
    }

    // 엔티티 값 변경을 위한 메서드 추가
    public void updateItem(String name, Integer price, Integer stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
    }
}
