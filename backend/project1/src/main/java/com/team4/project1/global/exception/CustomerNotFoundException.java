package com.team4.project1.global.exception;

/**
 * 고객을 찾을 수 없을 때 발생하는 예외 클래스입니다.
 * 고객 ID를 기반으로 고객을 조회하려 했지만, 해당 고객을 찾을 수 없을 때 발생합니다.
 */
public class CustomerNotFoundException extends RuntimeException {

    /**
     * 지정된 고객 ID로 고객을 찾을 수 없을 때 발생하는 예외를 생성합니다.
     * @param id 고객 ID
     */
    public CustomerNotFoundException(Long id) {
        super("해당 고객을 찾을 수 없습니다. (ID: " + id + ")");
    }

    /**
     * 지정된 메시지를 사용하여 고객을 찾을 수 없을 때 발생하는 예외를 생성합니다.
     * @param message 예외 메시지
     */
    public CustomerNotFoundException(String message) {
        super(message);
    }
}
