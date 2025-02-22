package com.team4.project1;

import com.team4.project1.domain.customer.repository.CustomerRepository;
import com.team4.project1.domain.customer.service.CustomerService;
import com.team4.project1.global.Rq;
import com.team4.project1.global.security.CustomAuthenticationFilter;
import com.team4.project1.global.security.CustomUserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class SpringSecurityTest {

    @Autowired
    private MockMvc mvc;

    @InjectMocks
    private CustomUserDetailService customUserDetailService;

    @InjectMocks
    private CustomAuthenticationFilter customAuthenticationFilter;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerService customerService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Autowired
    private Rq rq;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

}
