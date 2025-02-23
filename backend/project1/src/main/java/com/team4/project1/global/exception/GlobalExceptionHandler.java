package com.team4.project1.global.exception;

import com.team4.project1.global.dto.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 고객을 찾을 수 없을 때 예외 처리
    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ResponseDto<String>> handleCustomerNotFoundException(CustomerNotFoundException ex) {
        log.warn("CustomerNotFoundException: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ResponseDto.notFound(ex.getMessage()));
    }

    // 아이템을 찾을 수 없을 때 예외 처리
    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<ResponseDto<String>> handleItemNotFoundException(ItemNotFoundException ex) {
        log.warn("ItemNotFoundException: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ResponseDto.notFound(ex.getMessage()));
    }

    // 유효하지 않은 요청 데이터 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.warn("Validation Error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseDto.badRequest(ex.getMessage()));
    }

    // 잘못된 HTTP 메서드 사용 예외 처리
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ResponseDto<String>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        log.warn("Method Not Allowed: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ResponseDto.methodNotAllowed(ex.getMessage()));
    }

    // 예상하지 못한 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<String>> handleGeneralException(Exception ex) {
        log.error("Unhandled Exception: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseDto.internalServerError("An unexpected error occurred"));
    }
    // 재고 부족 예외 처리
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
}
