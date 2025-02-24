package com.team4.project1.domain.item.dto;

import com.team4.project1.domain.item.entity.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 상품의 정보를 담는 데이터 전송 객체(DTO)입니다.
 * 아이디, 이름, 가격, 상품의 재고를 담고있습니다.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {

    /**
     * 상품의 ID
     */
    private Long id;

    /**
     * 상품의 이름
     */
    private String name;

    /**
     * 상품의 가격
     */
    private Integer price;

    /**
     * 상품의 재고 수량
     */
    private Integer stock;

    /**
     * Item 엔티티를 ItemDto로 변환하는 메서드입니다.
     * @param item 변환할 Item 엔티티 객체
     * @return 변환된 ItemDto 객체를 반환합니다.
     */
    public static ItemDto from(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getPrice(),
                item.getStock()
        );
    }

    /**
     * 주어진 파라미터로 ItemDto 객체를 생성하는 메서드입니다.
     * @param id 상품 ID
     * @param name 상품 이름
     * @param price 상품 가격
     * @param stock 상품 재고 수량
     * @return 생성된 ItemDto 객체를 반환합니다.
     */
    public static ItemDto of(Long id, String name, Integer price, Integer stock) {
        return new ItemDto(id, name, price, stock);
    }
}
