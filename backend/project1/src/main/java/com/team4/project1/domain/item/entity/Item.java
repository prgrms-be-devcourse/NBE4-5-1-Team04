package com.team4.project1.domain.item.entity;

import com.team4.project1.domain.item.dto.ItemDto;
import jakarta.persistence.*;
import lombok.*;

/**
 * 고객의 정보를 저장하는 엔티티 클래스입니다.
 * 아이디, 사용자 이름, 비밀번호, apikey, 고객 이름, 이메일을 담고 있습니다.
 * 고객은 여러 개의 주문을 가질 수 있습니다.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

    /**
     * 상품의 ID
     * 데이터베이스에서 자동으로 증가하는 값을 할당합니다.
     * id는 객체 생성 시에만 값이 설정되고 이후에는 변경이 불가능합니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    /**
     * 상품의 이름
     * 비어 있을 수 없고 필수로 값을 입력해야합니다
     */
    @Column(nullable = false)
    private String name;

    /**
     * 상품의 가격
     * 비어 있을 수 없고 필수로 값을 입력해야합니다.
     */
    @Column(nullable = false)
    private Integer price;

    /**
     * 상품의 재고 수량
     * 비어 있을 수 없고 필수로 값을 입력해야합니다.
     */
    @Column(nullable = false)
    private Integer stock;

    /**
     * 상품을 생성할 때 사용하는 생성자입니다.
     * @param name 상품의 이름
     * @param price 상품 가격
     */
    public Item(String name, Integer price) {
        this.name = name;
        this.price = price;
    }

    /**
     * ItemDto 객체를 기반으로 새로운 Item 엔티티를 생성하는 메서드입니다.
     * @param itemDto ItemDto객체
     * @return 생성된 Item 엔티티 객체를 반환합니다.
     */
    public static Item fromDto(ItemDto itemDto) {
        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getPrice(), itemDto.getStock());
    }
}
