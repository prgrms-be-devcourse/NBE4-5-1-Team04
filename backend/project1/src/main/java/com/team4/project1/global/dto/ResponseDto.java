package com.team4.project1.global.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * API 응답을 위한 DTO 클래스입니다.
 * HTTP 상태 코드, 메시지, 데이터를 포함한 표준화된 응답 구조를 제공합니다.
 * @param <T> 응답 데이터의 타입
 */
@Getter
@AllArgsConstructor
public class ResponseDto<T> {
    private String code;   // HTTP 상태 코드
    private String message; // 상태 메시지
    private T data;        // 응답 데이터

    /**
     * code와 message만을 사용하여 응답을 생성하는 생성자입니다.
     * @param code    HTTP 상태 코드
     * @param message 상태 메시지
     */
    public ResponseDto(String code, String message) {
        this(code, message, null);
    }

    /**
     * 응답을 생성하는 메소드입니다.
     * 응답 코드, 메시지 및 데이터를 사용하여 {@link ResponseDto} 객체를 생성합니다.
     * @param code    HTTP 상태 코드
     * @param message 상태 메시지
     * @param data    응답 데이터
     * @param <T>     데이터 타입
     * @return {@link ResponseDto} 객체를 반환합니다.
     */
    public static <T> ResponseDto<T> of(String code, String message, T data) {
        return new ResponseDto<>(code, message, data);
    }

    /**
     * 성공적인 응답을 생성하는 메소드입니다.
     * HTTP 상태 코드 200(OK)과 해당 메시지를 설정합니다.
     * @param data 응답 데이터
     * @param <T>  데이터 타입
     * @return HTTP 200 OK 응답을 반환합니다.
     */
    public static <T> ResponseDto<T> ok(T data) {
        return new ResponseDto<>(HttpStatus.OK.value() + "", HttpStatus.OK.getReasonPhrase(), data);
    }

    /**
     * 잘못된 요청에 대한 응답을 생성하는 메소드입니다.
     * HTTP 상태 코드 400(BAD_REQUEST)과 해당 메시지를 설정합니다.
     * @param data 응답 데이터
     * @param <T>  데이터 타입
     * @return HTTP 400 BAD_REQUEST 응답을 반환합니다.
     */
    public static <T> ResponseDto<T> badRequest(T data) {
        return new ResponseDto<>(HttpStatus.BAD_REQUEST.value() + "", HttpStatus.BAD_REQUEST.getReasonPhrase(), data);
    }

    /**
     * 요청한 리소스를 찾을 수 없을 때의 응답을 생성하는 메소드입니다.
     * HTTP 상태 코드 404(NOT_FOUND)와 해당 메시지를 설정합니다.
     * @param data 응답 데이터
     * @param <T>  데이터 타입
     * @return HTTP 404 NOT_FOUND 응답을 반환합니다.
     */
    public static <T> ResponseDto<T> notFound(T data) {
        return new ResponseDto<>(HttpStatus.NOT_FOUND.value() + "", HttpStatus.NOT_FOUND.getReasonPhrase(), data);
    }

    /**
     * 지원하지 않는 HTTP 메서드에 대한 응답을 생성하는 메소드입니다.
     * HTTP 상태 코드 405(METHOD_NOT_ALLOWED)와 해당 메시지를 설정합니다.
     * @param data 응답 데이터
     * @param <T>  데이터 타입
     * @return HTTP 405 METHOD_NOT_ALLOWED 응답을 반환합니다.
     */
    public static <T> ResponseDto<T> methodNotAllowed(T data) {
        return new ResponseDto<>(HttpStatus.METHOD_NOT_ALLOWED.value() + "", HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(), data);
    }

    /**
     * 서버 오류에 대한 응답을 생성하는 메소드입니다.
     * HTTP 상태 코드 500(INTERNAL_SERVER_ERROR)와 해당 메시지를 설정합니다.
     * @param data 응답 데이터
     * @param <T>  데이터 타입
     * @return HTTP 500 INTERNAL_SERVER_ERROR 응답을 반환합니다.
     */
    public static <T> ResponseDto<T> internalServerError(T data) {
        return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value() + "", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), data);
    }
}
