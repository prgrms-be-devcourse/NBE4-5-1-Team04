package com.team4.project1.global.exception;

import com.team4.project1.global.dto.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외 처리 클래스입니다.
 * 애플리케이션에서 발생하는 다양한 예외를 처리하여 적절한 HTTP 응답을 반환합니다.
 * 각 예외는 로그에 기록되며, 해당 예외에 맞는 HTTP 상태 코드와 메시지가 포함된 응답을 반환합니다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 고객을 찾을 수 없을 때 발생하는 예외를 처리합니다.
     * @param ex 발생한 예외
     * @return "고객을 찾을 수 없습니다." 메시지와 함께 404 상태 코드가 포함된 응답을 반환합니다.
     */
    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ResponseDto<String>> handleCustomerNotFoundException(CustomerNotFoundException ex) {
        log.warn("CustomerNotFoundException: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ResponseDto.notFound(ex.getMessage()));
    }

    /**
     * 아이템을 찾을 수 없을 때 발생하는 예외를 처리합니다.
     * @param ex 발생한 예외
     * @return "아이템을 찾을 수 없습니다." 메시지와 함께 404 상태 코드가 포함된 응답을 반환합니다.
     */
    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<ResponseDto<String>> handleItemNotFoundException(ItemNotFoundException ex) {
        log.warn("ItemNotFoundException: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ResponseDto.notFound(ex.getMessage()));
    }

    /**
     * 잘못된 요청 본문으로 인해 발생한 예외를 처리합니다.
     * @param ex 발생한 예외
     * @return "잘못된 요청" 메시지와 함께 400 상태 코드가 포함된 응답을 반환합니다.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.warn("Validation Error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseDto.badRequest(ex.getMessage()));
    }

    /**
     * 지원되지 않는 HTTP 메서드 호출에 대해 발생한 예외를 처리합니다.
     * @param ex 발생한 예외
     * @return "지원되지 않는 메서드" 메시지와 함께 405 상태 코드가 포함된 응답을 반환합니다.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ResponseDto<String>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        log.warn("Method Not Allowed: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ResponseDto.methodNotAllowed(ex.getMessage()));
    }

    /**
     * 일반적인 예외를 처리합니다.
     * 예상치 못한 오류가 발생한 경우, 500 상태 코드와 함께 "예상치 못한 오류가 발생했습니다." 메시지를 반환합니다.
     * @param ex 발생한 예외
     * @return "예상치 못한 오류가 발생했습니다." 메시지와 함께 500 상태 코드가 포함된 응답을 반환합니다.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<String>> handleGeneralException(Exception ex) {
        log.error("Unhandled Exception: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseDto.internalServerError(ex.getMessage()));
    }

    /**
     * 재고 부족 예외를 처리합니다.
     * @param ex 발생한 예외
     * @return "재고 부족" 메시지와 함께 400 상태 코드가 포함된 응답을 반환합니다.
     */
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ResponseDto<String>> handleInsufficientStockException(InsufficientStockException ex) {
        log.warn("InsufficientStockException: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseDto.badRequest(ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ResponseDto<String>> handleIllegalStateException(IllegalStateException ex) {
        log.warn("IllegalStateException: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseDto.badRequest(ex.getMessage()));
    }

    /**
     * 사용자가 권한이 없을 때 발생하는 예외를 처리합니다.
     * @param ex 발생한 예외
     * @return "권한 없음" 메시지와 함께 403 상태 코드가 포함된 응답을 반환합니다.
     */
    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ResponseDto<String>> handleUnauthorizedAccessException(UnauthorizedAccessException ex) {
        log.warn("UnauthorizedAccessException: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN) // 403 상태 코드
                .body(ResponseDto.of(
                        HttpStatus.FORBIDDEN.value() + "",
                        ex.getMessage(),
                        null
                ));
    }
}
