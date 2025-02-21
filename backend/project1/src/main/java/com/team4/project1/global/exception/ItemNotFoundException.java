package com.team4.project1.global.exception;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(Integer id) {

        super("해당 상품을 찾을 수 없습니다. (ID: " + id + ")");
    }
}
