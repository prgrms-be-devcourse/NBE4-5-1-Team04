package com.team4.project1.domain.order.dto;

import com.team4.project1.domain.order.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 주문 정보를 전달하기 위한 DTO(Data Transfer Object) 클래스입니다.
 * 주문 ID, 주문 날짜, 총 주문 금액을 포함합니다.
 */
@Getter
@AllArgsConstructor
public class OrderDto {

    /**주문 ID*/
    private Long id; // 주문 ID

    /** 주문이 생성된 날짜 및 시간*/
    private LocalDateTime date; // 주문 날짜

    /** 주문 총액( 전체 주문 가격) */
    private Long totalPrice; // 주문 총액

    private String deliveryStatus;

    /**
     * {@Link Order}엔티티 객체를 DTO로 변환하는 메서드입니다.
     * @param order 변환할 주문 엔티티 객체
     * @return 변환된 {@Link OrderDto}  객체를 반환합니다.
     */
    public static OrderDto from(Order order) {
        return new OrderDto(
                order.getId(),
                order.getDate(),
                order.getTotalPrice(),
                order.getOrderStatus().name()
        );
    }

    /**
     * 주어진 값으로 새로운 OrderDto 객체를 생성하는 메서드입니다.
     * @param id  주문 ID
     * @param date 주문 날짜 및 시간
     * @param totalPrice 주문 총액
     * @return 생성된 {@Link OrderDto} 객체를 반환합니다.
     */
    public static OrderDto of(Long id, LocalDateTime date, Long totalPrice, String deliveryStatus) {
        return new OrderDto(id, date, totalPrice, deliveryStatus);
    }
}
