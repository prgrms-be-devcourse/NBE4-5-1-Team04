package com.team4.project1.security;

import com.team4.project1.domain.customer.entity.Customer;
import com.team4.project1.domain.customer.service.CustomerService;
import com.team4.project1.global.Rq;
import com.team4.project1.global.security.CustomAuthenticationFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomAuthenticationFilterTest {

    @Mock
    private Rq rq;

    @Mock
    private CustomerService customerService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private CustomAuthenticationFilter customAuthenticationFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("인증 헤더가 없는 경우 필터 통과")
    void t1() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);

        customAuthenticationFilter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(customerService);
    }

    @Test
    @DisplayName("잘못된 형식의 인증 헤더가 있을 때 필터 통과")
    void t2() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("InvalidToken");

        customAuthenticationFilter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(customerService);
    }

    @Test
    @DisplayName("유효한 API Key가 있을 때 필터 통과 및 사용자 인증")
    void t3() throws ServletException, IOException {
        String apiKey = "test";

        customerService.join("test", "test1234", "테스트", "test@test.com");
        Optional<Customer> customer = customerService.findByUsername(apiKey);

        when(customerService.findByApiKey(apiKey)).thenReturn(customer);

        System.out.println(customerService.getAllCustomers());

        doReturn("Bearer " + apiKey).when(request).getHeader("Authorization");

        customAuthenticationFilter.doFilter(request, response, filterChain);

        verify(rq, times(1)).setLogin("test");
        verify(filterChain, times(1)).doFilter(request, response);
    }

}
