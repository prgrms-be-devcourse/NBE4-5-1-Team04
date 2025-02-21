package com.team4.project1.domain.customer.service;

import com.team4.project1.domain.customer.entity.Customer;
import com.team4.project1.domain.customer.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApiV1CustomerControllerTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .id(1L)
                .username("testUser")
                .password("password123")
                .name("Test Name")
                .email("test@example.com")
                .build();
    }

    @Test
    @DisplayName("새로운 고객을 등록할 수 있다.")
    void joinCustomer() {
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        Customer savedCustomer = customerService.join("testUser", "password123", "Test Name", "test@example.com");

        assertThat(savedCustomer).isNotNull();
        assertThat(savedCustomer.getUsername()).isEqualTo("testUser");
        assertThat(savedCustomer.getEmail()).isEqualTo("test@example.com");
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

}
