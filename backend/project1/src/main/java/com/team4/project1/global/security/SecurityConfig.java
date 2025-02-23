package com.team4.project1.global.security;

import com.team4.project1.global.dto.ResponseDto;
import com.team4.project1.standard.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
                                )
                                .permitAll()
                                .requestMatchers(
                                        "/api/v1/customer/login",
                                        "/api/v1/customer/join",
                                        "/api/v1/customer"
                                )
                                .permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/v1/orders")  // POST 요청 허용
                                .permitAll()
                                .requestMatchers(
                                        "/api/v1/orders/{orderId:\\d+}"
                                )
                                .permitAll()
                                .requestMatchers("/api/*/**")
                                .authenticated()
                                .anyRequest()
                                .permitAll()
                )
                .csrf(csrf -> csrf.disable()) // ✅ CSRF 비활성화
                .addFilterBefore(
                        customAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )
                .exceptionHandling(
                        exceptionHandling -> exceptionHandling
                                .authenticationEntryPoint(
                                        (request, response, authException) -> {
                                            response.setContentType("application/json;charset=UTF-8");
                                            response.setStatus(401);
                                            response.getWriter().write(
                                                    Ut.Json.toString(
                                                            new ResponseDto(
                                                                    "401-1",
                                                                    "잘못된 인증키 입니다."
                                                            )
                                                    )
                                            );
                                        }
                                )
                                .accessDeniedHandler(
                                        (request, response, accessDeniedException) -> {
                                            response.setContentType("application/json;charset=UTF-8");
                                            response.setStatus(403);
                                            response.getWriter().write(
                                                    Ut.Json.toString(
                                                            new ResponseDto(
                                                                    "403-1",
                                                                    "접근 권한이 없습니다."
                                                            )
                                                    )
                                            );
                                        }
                                )
                );
        return http.build();
    }
}