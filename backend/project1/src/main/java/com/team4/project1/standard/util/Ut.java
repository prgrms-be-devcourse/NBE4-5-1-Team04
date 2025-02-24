package com.team4.project1.standard.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * 다양한 유틸리티 메서드를 제공하는 클래스입니다.
 * 이 클래스는 JSON 관련 기능을 포함한 여러 유틸리티 기능을 제공합니다.
 */
public class Ut {
    /**
     * JSON 관련 기능을 처리하는 내부 클래스입니다.
     * 이 클래스는 객체를 JSON 문자열로 변환하는 기능을 제공합니다.
     */
    public static class Json {
        // Jackson ObjectMapper 인스턴스를 재사용하기 위한 변수
        private static final ObjectMapper objectMapper = new ObjectMapper();
        /**
         * 주어진 객체를 JSON 문자열로 변환합니다.
         * 객체를 JSON 형식으로 변환하여 반환합니다. 변환에 실패하면 {@link RuntimeException}이 발생합니다.
         * @param obj 변환할 객체
         * @return 변환된 JSON 문자열을 반환합니다.
         * @throws RuntimeException JSON 변환 중 오류가 발생하면 예외 발생
         */
        public static String toString(Object obj) {
            try {
                // 객체를 JSON 문자열로 변환하여 반환
                return objectMapper.writeValueAsString(obj);
            } catch (JsonProcessingException e) {
                // 변환 중 오류가 발생하면 RuntimeException으로 래핑하여 던짐
                throw new RuntimeException(e);
            }
        }
    }
}
