package com.team4.project1.global.exception;

/**
 * 특정 상품을 찾을 수 없을 때 발생하는 예외입니다.
 * 주어진 상품 ID로 해당 상품을 찾을 수 없을 경우 이 예외가 발생합니다.
 */
public class ItemNotFoundException extends RuntimeException {

    /**
     * 주어진 상품 ID로 상품을 찾을 수 없을 때 발생하는 예외를 생성합니다.
     * @param id 찾을 수 없는 상품의 ID
     */
    public ItemNotFoundException(Long id) {
        super("해당 상품을 찾을 수 없습니다. (ID: " + id + ")");
    }

    /**
     * 사용자 정의 메시지로 예외를 생성합니다.
     * @param message 예외 메시지
     */
    public ItemNotFoundException(String message) {
        super(message);
    }
}
