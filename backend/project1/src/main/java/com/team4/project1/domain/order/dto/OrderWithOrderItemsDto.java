package com.team4.project1.domain.order.dto;

import com.team4.project1.domain.order.entity.Order;
import com.team4.project1.domain.order.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 주문과 함께 주문 항목 목록을 포함하는 DTO 클래스입니다.
 * OrderWithOrderItemsDto는 주문의 기본 정보(주문 ID, 날짜, 총 가격, 배송 상태)와 함께
 * 해당 주문에 포함된 모든 주문 항목 리스트를 제공합니다.
 */
@Getter
@AllArgsConstructor
public class OrderWithOrderItemsDto {
    /** 주문 ID */
    private Long id; // 주문 ID
    /** 주문 날짜 */
    private LocalDateTime date; // 주문 날짜
    /** 총 주문 금액 */
    private Long totalPrice; // 총 가격
    /** 배송 상태 */
    private String deliveryStatus; // 배송 상태
    /** 주문 항목 목록 */
    private List<OrderItemDto> orderedItems; // 주문 항목 목록

    /**
     * Order 엔티티를 OrderWithOrderItemsDto로 변환하는 메서드입니다.
     * 주문 정보와 해당 주문의 모든 주문 항목을 포함하여 DTO로 변환합니다.
     * @param order 변환할 {@link Order} 엔티티 객체
     * @return 변환된 {@link OrderWithOrderItemsDto} 객체를 반환합니다.
     */
    public static OrderWithOrderItemsDto from(Order order) {
        return new OrderWithOrderItemsDto(
                order.getId(),
                order.getDate(),
                order.getTotalPrice(),
                order.getOrderStatus().name(),
                order.getOrderItems().stream()
                        .map(OrderItemDto::from)
                        .toList()
        );
    }

    /**
     * 주어진 값으로 새로운 OrderWithOrderItemsDto 객체를 생성하는 메서드입니다.
     * 개별 필드를 직접 입력하여 DTO를 생성할 때 사용할 수 있습니다.
     * @param id 주문 ID
     * @param date 주문 날짜
     * @param totalPrice 총 주문 금액
     * @param deliveryStatus 배송 상태
     * @param orderItems 주문 항목 목록
     * @return 생성된 {@link OrderWithOrderItemsDto} 객체를 반환합니다.
     */
    public static OrderWithOrderItemsDto of(Long id, LocalDateTime date, Long totalPrice, String deliveryStatus, List<OrderItem> orderItems) {
        return new OrderWithOrderItemsDto(id, date, totalPrice, deliveryStatus, orderItems.stream().map(OrderItemDto::from).toList());
    }
}
