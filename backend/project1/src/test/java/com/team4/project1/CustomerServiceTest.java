package com.team4.project1;

import com.team4.project1.domain.customer.dto.CustomerDto;
import com.team4.project1.domain.customer.entity.Customer;
import com.team4.project1.domain.customer.repository.CustomerRepository;
import com.team4.project1.domain.customer.service.CustomerService;
import com.team4.project1.global.exception.CustomerNotFoundException;
import com.team4.project1.global.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Mockito 확장 기능 활성화
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository; // 가짜 객체 (Mock) 생성

    @Mock
    private EmailService emailService;

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
                .apiKey("testApiKey")
                .build();
    }

    @Test
    @DisplayName("새로운 고객을 등록할 수 있다.")
    void joinCustomer() {
        // Given
        given(customerRepository.save(any(Customer.class))).willReturn(customer);
        Mockito.doNothing().when(emailService).sendSimpleEmail(
                any(String.class),
                any(String.class),
                any(String.class)
        );

        // When
        Customer savedCustomer = customerService.join(
                customer.getUsername(),
                customer.getPassword(),
                customer.getName(),
                customer.getEmail()
        );

        // Then
        assertThat(savedCustomer.getUsername()).isEqualTo(customer.getUsername());
        assertThat(savedCustomer.getPassword()).isEqualTo(customer.getPassword());
        assertThat(savedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(savedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(savedCustomer.getApiKey()).isEqualTo(customer.getApiKey());
        then(customerRepository).should().save(any(Customer.class));
        then(emailService).should().sendSimpleEmail(
                any(String.class),
                any(String.class),
                any(String.class)
        );
    }

    @Test
    @DisplayName("고객 ID로 고객을 조회할 수 있다.")
    void getCustomerById() {
        // Given
        given(customerRepository.findById(customer.getId())).willReturn(Optional.of(customer));

        // When
        Customer foundCustomer = customerService.getCustomerById(customer.getId()).orElseThrow();

        // Then
        assertThat(foundCustomer.getId()).isEqualTo(customer.getId());
        assertThat(foundCustomer.getUsername()).isEqualTo(customer.getUsername());
        assertThat(foundCustomer.getPassword()).isEqualTo(customer.getPassword());
        assertThat(foundCustomer.getName()).isEqualTo(customer.getName());
        assertThat(foundCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(foundCustomer.getApiKey()).isEqualTo(customer.getApiKey());
        then(customerRepository).should().findById(1L);
    }

    @Test
    @DisplayName("존재하지 않는 고객 ID 조회 시 예외가 발생한다.")
    void getCustomerByIdNotFound() {
        // Given
        given(customerRepository.findById(0L)).willReturn(Optional.empty());

        // When
        Exception exception = assertThrows(CustomerNotFoundException.class, () -> {
            customerService.getCustomerById(0L);
        });

        // Then
        assertThat(exception.getMessage()).isEqualTo("해당 고객을 찾을 수 없습니다. (ID: " + 0L + ")");
        then(customerRepository).should().findById(0L);
    }

    @Test
    @DisplayName("고객 username으로 고객을 조회할 수 있다.")
    void getCustomerByUsername() {
        // Given
        given(customerRepository.findByUsername(customer.getUsername())).willReturn(Optional.of(customer));

        // When
        Customer foundCustomer = customerService.findByUsername(customer.getUsername()).orElseThrow();

        // Then
        assertThat(foundCustomer.getId()).isEqualTo(customer.getId());
        assertThat(foundCustomer.getUsername()).isEqualTo(customer.getUsername());
        assertThat(foundCustomer.getPassword()).isEqualTo(customer.getPassword());
        assertThat(foundCustomer.getName()).isEqualTo(customer.getName());
        assertThat(foundCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(foundCustomer.getApiKey()).isEqualTo(customer.getApiKey());
        then(customerRepository).should().findByUsername(customer.getUsername());
    }

    @Test
    @DisplayName("존재하지 않는 고객 username 조회 시 예외가 발생한다.")
    void getCustomerByNonexistentUsername() {
        // Given
        String testUsername = "testUsername";
        given(customerRepository.findByUsername(testUsername))
                .willReturn(Optional.empty());

        // When
        Exception exception = assertThrows(CustomerNotFoundException.class, () -> {
            customerService.findByUsername(testUsername);
        });

        // Then
        assertThat(exception.getMessage()).isEqualTo("해당 고객을 찾을 수 없습니다. (ID: " + "someUsername" + ")");
        then(customerRepository).should().findByUsername(testUsername);
    }

    @Test
    @DisplayName("고객 API키로 고객을 조회할 수 있다.")
    void getCustomerByApiKey() {
        // Given
        given(customerRepository.findByApiKey(customer.getApiKey())).willReturn(Optional.of(customer));

        // When
        Customer foundCustomer = customerService.findByApiKey(customer.getApiKey()).orElseThrow();

        // Then
        assertThat(foundCustomer.getId()).isEqualTo(customer.getId());
        assertThat(foundCustomer.getUsername()).isEqualTo(customer.getUsername());
        assertThat(foundCustomer.getPassword()).isEqualTo(customer.getPassword());
        assertThat(foundCustomer.getName()).isEqualTo(customer.getName());
        assertThat(foundCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(foundCustomer.getApiKey()).isEqualTo(customer.getApiKey());
        then(customerRepository).should().findByApiKey(customer.getApiKey());
    }

    @Test
    @DisplayName("존재하지 않는 고객 API키로 조회 시 예외가 발생한다.")
    void getCustomerByNonexistentApiKey() {
        // Given
        String testApiKey = "testApiKey";
        given(customerRepository.findByUsername(testApiKey))
                .willReturn(Optional.empty());

        // When
        Exception exception = assertThrows(CustomerNotFoundException.class, () -> {
            customerService.findByApiKey(testApiKey);
        });

        // Then
        assertThat(exception.getMessage()).isEqualTo("해당 고객을 찾을 수 없습니다. (ID: " + "someUsername" + ")");
        then(customerRepository).should().findByApiKey(testApiKey);
    }

    @Test
    @DisplayName("고객의 전체 목록을 조회할 수 있다.")
    void getAllCustomers() {
        // Given
        given(customerRepository.findAll()).willReturn(List.of(customer));

        // When
        List<Customer> customers = customerService.getAllCustomers();

        // Then
        assertThat(customers).isNotEmpty();
        assertThat(customers).hasSize(1);
        assertThat(customers.getFirst().getId()).isEqualTo(customer.getId());
        assertThat(customers.getFirst().getUsername()).isEqualTo(customer.getUsername());
        assertThat(customers.getFirst().getPassword()).isEqualTo(customer.getPassword());
        assertThat(customers.getFirst().getName()).isEqualTo(customer.getName());
        assertThat(customers.getFirst().getEmail()).isEqualTo(customer.getEmail());
        assertThat(customers.getFirst().getApiKey()).isEqualTo(customer.getApiKey());
        then(customerRepository).should().findAll();
    }

    @Test
    @DisplayName("고객 정보를 수정할 수 있다.")
    void updateCustomer() {
        CustomerDto updatedDto = new CustomerDto("Updated Name", "updated@example.com");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        Customer updatedCustomer = customerService.updateCustomer(1L, updatedDto);

        assertThat(updatedCustomer.getName()).isEqualTo("Updated Name");
        assertThat(updatedCustomer.getEmail()).isEqualTo("updated@example.com");
        then(customerRepository).should().findById(1L);
    }

    @Test
    @DisplayName("존재하지 않는 고객의 정보를 수정 시 예외가 발생한다.")
    void updateNonexistentCustomer() {
        // Given
        given(customerRepository.findById(0L)).willReturn(Optional.empty());

        // When
        Exception exception = assertThrows(CustomerNotFoundException.class, () -> {
           customerService.updateCustomer(0L, new CustomerDto("", ""));
        });

        // Then
        assertThat(exception.getMessage()).isEqualTo("해당 고객을 찾을 수 없습니다. (ID: " + 0L + ")");
        then(customerRepository).should().findById(0L);
    }

    @Test
    @DisplayName("고객 수를 조회할 수 있다.")
    void countCustomers() {
        // Given
        given(customerRepository.count()).willReturn(5L);

        // When
        long customerCount = customerService.count();

        // Then
        assertThat(customerCount).isEqualTo(5);
        then(customerRepository).should().count();
    }
}
