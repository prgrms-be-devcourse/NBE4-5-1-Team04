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


@Component
@RequiredArgsConstructor
@RequestScope
public class Rq {

    private final HttpServletRequest request;
    private final CustomerService customerService;


    public Customer getAuthenticatedActor() {
        String authorizationValue = request.getHeader("Authorization");
        String apiKey = authorizationValue.substring("Bearer ".length());

        Customer customer = customerService.findByApiKey(apiKey);

        if (customer == null) {
            throw new AuthenticationException("잘못된 인증키 입니다.") {};
        }

        return customer;
    }


    public void setLogin(String username) {
        UserDetails user = new User(username, "", List.of());

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
        );
    }


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
