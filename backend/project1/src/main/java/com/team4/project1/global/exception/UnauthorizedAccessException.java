package com.team4.project1.global.exception;

/**
 * 사용자가 인증되지 않았거나 권한이 없을 때 발생하는 예외입니다.
 * 이 예외는 사용자가 요청한 작업을 수행할 권한이 없을 경우 발생합니다.
 */
public class UnauthorizedAccessException extends RuntimeException {

    /**
     * 사용자 정의 메시지로 예외를 생성합니다.
     * @param message 예외 메시지
     */
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
