package com.team4.project1.global.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
@AllArgsConstructor
public class ResponseDto<T> {
    private String code;   // HTTP 상태 코드
    private String message; // 상태 메시지
    private T data;        // 응답 데이터


    public ResponseDto(String code, String message) {
        this(code, message, null);
    }


    public static <T> ResponseDto<T> of(String code, String message, T data) {
        return new ResponseDto<>(code, message, data);
    }


    public static <T> ResponseDto<T> ok(T data) {
        return new ResponseDto<>(HttpStatus.OK.value() + "", HttpStatus.OK.getReasonPhrase(), data);
    }


    public static <T> ResponseDto<T> badRequest(T data) {
        return new ResponseDto<>(HttpStatus.BAD_REQUEST.value() + "", HttpStatus.BAD_REQUEST.getReasonPhrase(), data);
    }


    public static <T> ResponseDto<T> notFound(T data) {
        return new ResponseDto<>(HttpStatus.NOT_FOUND.value() + "", HttpStatus.NOT_FOUND.getReasonPhrase(), data);
    }


    public static <T> ResponseDto<T> methodNotAllowed(T data) {
        return new ResponseDto<>(HttpStatus.METHOD_NOT_ALLOWED.value() + "", HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(), data);
    }


    public static <T> ResponseDto<T> internalServerError(T data) {
        return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value() + "", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), data);
    }
}
