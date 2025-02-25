package com.team4.project1.global.exception;

/**
 * 유효하지 않은 주문 수량 예외를 처리하는 클래스입니다.
 * 주문 수량이 1 이상이어야 하는 등의 조건을 위반할 경우 발생하는 예외입니다.
 */
public class InvalidOrderQuantityException extends RuntimeException {

    /**
     * 유효하지 않은 주문 수량 예외를 생성하는 생성자입니다.
     * @param message 예외 메시지
     */
    public InvalidOrderQuantityException(String message) {
        super(message);
    }
}
