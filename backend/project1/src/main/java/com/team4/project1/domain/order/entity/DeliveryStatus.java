package com.team4.project1.domain.order.entity;


/**
 * 주문의 배송 상태를 나타냅니다.
 * 이 열거형은 주문이 현재 어떤 배송 상태에 있는지를 나타내며,
 * 주문이 처리 중인지 또는 이미 배송되었는지를 구분합니다.
 */
public enum DeliveryStatus {
    /** 주문이 접수되어 처리 중인 상태 */
    PROCESSING,

    /** 주문이 배송된 상태 */
    SHIPPED
}
