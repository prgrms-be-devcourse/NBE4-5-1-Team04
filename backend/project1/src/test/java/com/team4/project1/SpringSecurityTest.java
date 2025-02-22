package com.team4.project1;

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
public class SpringSecurityTest {

    @InjectMocks
    private CustomUserDetailService customUserDetailService;

    @Mock
    private CustomerRepository customerRepository;

    // CustomUserDetailService Test

    @Test
    @DisplayName("CustomUserDetailService - User O")
    public void t1() {
        Customer customer = new Customer();
        customer.setUsername("test");
        customer.setPassword("test1234");

        when(customerRepository.findByUsername("test")).thenReturn(Optional.of(customer));

        UserDetails userDetails = customUserDetailService.loadUserByUsername("test");

        assertEquals("test", userDetails.getUsername());
        assertEquals("test1234", userDetails.getPassword());
    }

    @Test
    @DisplayName("CustomUserDetailService - User X")
    void t2() {
        when(customerRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                customUserDetailService.loadUserByUsername("unknown")
        );
    }

}
