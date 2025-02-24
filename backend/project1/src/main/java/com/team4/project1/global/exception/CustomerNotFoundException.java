package com.team4.project1.global.exception;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(Long id) {
        super("해당 고객을 찾을 수 없습니다. (ID: " + id + ")");
    }
    public CustomerNotFoundException(String message) {
        super(message);
    }
}