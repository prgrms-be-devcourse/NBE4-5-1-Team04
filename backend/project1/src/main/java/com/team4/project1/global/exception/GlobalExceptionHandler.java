package com.team4.project1.global.exception;


import com.team4.project1.global.dto.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //고객 조회 시 존재하지 않는 ID 입력 (CustomerNotFoundException)
    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ResponseDto<String>> handleCustomerNotFoundException(CustomerNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ResponseDto.notFound(ex.getMessage()));
    }

    //아이템 조회 시 존재하지 않는 ID 입력 (ItemNotFoundException)
    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<ResponseDto<String>> handleItemNotFoundException(ItemNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ResponseDto.notFound(ex.getMessage()));
    }
    //잘못된 요청 데이터 (MethodArgumentNotValidException)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ResponseDto.notFound(ex.getMessage()));
    }

    //잘못된 JSON 형식 (HttpMessageNotReadableException)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseDto<String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseDto.badRequest(ex.getMessage()));
    }

    //잘못된 HTTP 메서드 (HttpRequestMethodNotSupportedException)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ResponseDto<String>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ResponseDto.methodNotAllowed(ex.getMessage()));
    }

    //일반적인 예외 처리 (Exception)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<String>> handleGeneralException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseDto.internalServerError(ex.getMessage()));
    }
}
