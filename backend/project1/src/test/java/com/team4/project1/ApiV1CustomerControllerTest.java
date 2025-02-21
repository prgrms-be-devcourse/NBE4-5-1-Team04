package com.team4.project1;

import com.team4.project1.domain.customer.dto.CustomerDto;
import com.team4.project1.domain.customer.entity.Customer;
import com.team4.project1.domain.customer.repository.CustomerRepository;
import com.team4.project1.domain.customer.service.CustomerService;
import com.team4.project1.global.exception.CustomerNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Mockito 확장 기능 활성화
class ApiV1CustomerControllerTest {

    @Mock
    private CustomerRepository customerRepository; // 가짜 객체 (Mock) 생성

    @InjectMocks
    private CustomerService customerService; // customerRepository를 주입받을 실제 서비스 객체 생성

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

    @Test
    @DisplayName("고객 ID로 고객을 조회할 수 있다.")
    void getCustomerById() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        Customer foundCustomer = customerService.getCustomerById(1L).orElseThrow();

        assertThat(foundCustomer).isNotNull();
        assertThat(foundCustomer.getId()).isEqualTo(1L);
        assertThat(foundCustomer.getUsername()).isEqualTo("testUser");
        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("존재하지 않는 고객 ID 조회 시 예외가 발생한다.")
    void getCustomerByIdNotFound() {
        // Given
        Long nonExistentId = 2L;
        when(customerRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        Exception exception = assertThrows(CustomerNotFoundException.class, () -> {
            customerService.getCustomerById(nonExistentId);
        });

        assertThat(exception.getMessage()).isEqualTo("해당 고객을 찾을 수 없습니다. (ID: " + nonExistentId + ")");
        verify(customerRepository, times(1)).findById(nonExistentId);
    }


    @Test
    @DisplayName("고객의 전체 목록을 조회할 수 있다.")
    void getAllCustomers() {
        // Given
        when(customerRepository.findAll()).thenReturn(List.of(customer));

        // When
        List<Customer> customers = customerService.getAllCustomers();

        // Then
        assertThat(customers).isNotEmpty();
        assertThat(customers).hasSize(1);
        assertThat(customers.get(0).getUsername()).isEqualTo("testUser");

        verify(customerRepository, times(1)).findAll();
    }


    @Test
    @DisplayName("고객 정보를 수정할 수 있다.")
    void updateCustomer() {
        CustomerDto updatedDto = new CustomerDto("Updated Name", "updated@example.com");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        Customer updatedCustomer = customerService.updateCustomer(1L, updatedDto);

        assertThat(updatedCustomer.getName()).isEqualTo("Updated Name");
        assertThat(updatedCustomer.getEmail()).isEqualTo("updated@example.com");
        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("고객 수를 조회할 수 있다.")
    void countCustomers() {
        when(customerRepository.count()).thenReturn(5L);

        long customerCount = customerService.count();

        assertThat(customerCount).isEqualTo(5);
        verify(customerRepository, times(1)).count();
    }
}
