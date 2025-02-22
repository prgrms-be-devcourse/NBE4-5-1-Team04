package com.team4.project1.security;

import com.team4.project1.domain.customer.entity.Customer;
import com.team4.project1.domain.customer.repository.CustomerRepository;
import com.team4.project1.global.security.CustomUserDetailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailServiceTest {
    @InjectMocks
    private CustomUserDetailService customUserDetailService;

    @Mock
    private CustomerRepository customerRepository;

    @Test
    @DisplayName("사용자가 존재하는 경우")
    public void t1() {
        String username = "test";
        String password = "test1234";

        Customer customer = new Customer(username, password);

        when(customerRepository.findByUsername(username)).thenReturn(Optional.of(customer));

        UserDetails userDetails = customUserDetailService.loadUserByUsername(username);

        assertEquals(username, userDetails.getUsername());
        assertEquals(password, userDetails.getPassword());
    }

    @Test
    @DisplayName("사용자가 존재하지 않는 경우")
    public void t2() {
        String username = "unknown";

        when(customerRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                customUserDetailService.loadUserByUsername(username)
        );
    }
}
