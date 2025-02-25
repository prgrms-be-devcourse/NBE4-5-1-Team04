package com.team4.project1.global.init;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 애플리케이션 실행 시 기본 데이터를 초기화하는 클래스입니다.
 * 이 클래스는 고객, 상품, 주문 데이터를 초기화하며,
 * 기본 데이터가 없을 경우에만 실행됩니다.
 */
@Configuration
@RequiredArgsConstructor
public class BaseInitData {

    /**
     * 기존 데이터 삽입 로직 제거됨.
     * 필요하면 다른 초기화 작업 추가 가능.
     */
    @Bean
    public ApplicationRunner applicationRunner() {
        return args -> {
            System.out.println("초기 데이터가 data.sql에서 주입됩니다.");
        };
    }
}