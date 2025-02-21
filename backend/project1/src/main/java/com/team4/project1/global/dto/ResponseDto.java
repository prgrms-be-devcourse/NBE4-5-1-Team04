package com.team4.project1.global.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class ResponseDto<T> {
    private String code;
    private String message;
    private T data;

    public ResponseDto(String code, String message) {
        this(code, message, null);
    }

    public static <T> ResponseDto<T> of(String code, String message, T data) {
        return new ResponseDto<>(code, message, data);
    }


    // 2xx (성공 응답)
    public static <T> ResponseDto<T> ok(T data) {
        return new ResponseDto<>(HttpStatus.OK.value() + "", HttpStatus.OK.getReasonPhrase(), data);
    }


    // 4xx (클라이언트 오류 응답)
    public static ResponseDto<String> badRequest(String message) {
        return new ResponseDto<>(HttpStatus.BAD_REQUEST.value() + "", message, null);
    }

    public static ResponseDto<String> notFound(String message) {
        return new ResponseDto<>(HttpStatus.NOT_FOUND.value() + "", message, null);
    }

    public static ResponseDto<String> methodNotAllowed(String message) {
        return new ResponseDto<>(HttpStatus.METHOD_NOT_ALLOWED.value() + "", message, null);
    }


    // 5xx (서버 오류 응답)
    public static ResponseDto<String> internalServerError(String message) {
        return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value() + "", message, null);
    }
}
