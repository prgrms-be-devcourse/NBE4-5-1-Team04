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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationFilter customAuthenticationFilter;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(
                        (authorizeHttpRequests) -> authorizeHttpRequests
                                .requestMatchers(HttpMethod.GET,
                                        "/api/v1/items",
                                        "/api/v1/items/{itemId:\\d+}",
                                        "/api/v1/items/{itemId:\\d+}/image",
                                        "/api/v1/orders/**"
                                )
                                .permitAll()
                                .requestMatchers(
                                        "/api/v1/customer/login",
                                        "/api/v1/customer/join",
                                        "/api/v1/customer"
                                )
                                .permitAll()
                                .requestMatchers(HttpMethod.POST,
                                        "/api/v1/orders",
                                        "/api/v1/orders/{orderId:\\d+}/confirm",
                                        "/api/v1/items/{itemId:\\d+}/image")  // POST 요청 허용
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

    // ✅ CORS 설정을 위한 Bean 추가
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true); // 쿠키 포함 요청 허용
        configuration.setAllowedOrigins(List.of("http://localhost:3000")); // 허용할 프론트엔드 주소
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}