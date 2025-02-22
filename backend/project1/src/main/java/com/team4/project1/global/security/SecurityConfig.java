package com.team4.project1.global.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationFilter customAuthenticationFilter;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(
                        (authorizeHttpRequests) -> authorizeHttpRequests
                                .requestMatchers(HttpMethod.GET,
                                        "/api/v1/items",
                                        "/api/v1/items/{itemId:\\d+}",
                                        "/api/v1/orders/**"
                                ).permitAll()  // ✅ GET 요청은 모두 허용
                                .requestMatchers(HttpMethod.POST, "/api/v1/orders").permitAll() // ✅ 주문 생성 API 허용
                                .requestMatchers("/api/v1/customer/login", "/api/v1/customer/join").permitAll()
                                .anyRequest().authenticated() // ❗ 그 외 요청은 인증 필요
                )
                .csrf(csrf -> csrf.disable()) // ✅ CSRF 비활성화
                .addFilterBefore(
                        customAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }


}