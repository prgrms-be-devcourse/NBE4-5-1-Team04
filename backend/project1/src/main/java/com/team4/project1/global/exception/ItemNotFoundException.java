package com.team4.project1.global.exception;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(Long id) {
        super("해당 상품을 찾을 수 없습니다. (ID: " + id + ")");
    }
    public ItemNotFoundException(Long id, Integer availableStock) {
        super("상품 (ID: " + id + ")의 재고가 부족합니다. 현재 재고: " + availableStock);
    }
}
