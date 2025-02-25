package com.team4.project1.global;

import com.team4.project1.domain.customer.entity.Customer;
import com.team4.project1.domain.customer.service.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.List;
import java.util.Optional;

/**
 * 요청에 대한 인증된 사용자 정보를 처리하는 클래스입니다.
 * 이 클래스는 HTTP 요청에서 인증 정보를 추출하고, 인증된 사용자 정보를 가져오는 기능을 제공합니다.
 */
@Component
@RequiredArgsConstructor
@RequestScope
public class Rq {

    private final HttpServletRequest request;
    private final CustomerService customerService;

    /**
     * 인증된 사용자의 정보를 반환합니다.
     * HTTP 요청에서 "Authorization" 헤더를 확인하고, 유효한 API 키를 통해 인증된 사용자를 가져옵니다.
     * @return 인증된 사용자 정보 {@link Customer}을 반환합니다.
     * @throws AuthenticationException 인증 키가 유효하지 않으면 예외 발생
     */
    public Customer getAuthenticatedActor() {
        String authorizationValue = request.getHeader("Authorization");
        String apiKey = authorizationValue.substring("Bearer ".length());

        Customer customer = customerService.findByApiKey(apiKey);

        if (customer == null) {
            throw new AuthenticationException("잘못된 인증키 입니다.") {};
        }

        return customer;
    }

    /**
     * 주어진 사용자 이름으로 로그인 상태를 설정합니다.
     * 사용자 이름을 기반으로 {@link UsernamePasswordAuthenticationToken}을 생성하여, Spring Security의 인증 정보를 설정합니다.
     * @param username 로그인할 사용자의 이름
     */
    public void setLogin(String username) {
        UserDetails user = new User(username, "", List.of());

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
        );
    }

    /**
     * 현재 로그인된 사용자의 정보를 반환합니다.
     * Spring Security의 인증 정보를 기반으로 현재 로그인된 사용자의 {@link Customer} 객체를 반환합니다.
     * @return 현재 로그인된 사용자 정보 {@link Customer}을 반환합니다.
     * @throws AuthenticationException 인증되지 않은 경우 발생
     */
    public Customer getActor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new AuthenticationException("로그인이 필요합니다.") {};
        }

        UserDetails user = (UserDetails) authentication.getPrincipal();

        if (user == null) {
            throw new AuthenticationException("로그인이 필요합니다.") {};
        }

        String username = user.getUsername();

        return customerService.findByUsername(username);
    }
}
