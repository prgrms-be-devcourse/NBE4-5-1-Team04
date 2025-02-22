package com.team4.project1.domain.customer.service;

import com.team4.project1.domain.customer.dto.CustomerDto;
import com.team4.project1.domain.customer.entity.Customer;
import com.team4.project1.domain.customer.repository.CustomerRepository;
import com.team4.project1.global.exception.CustomerNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final EmailService emailService;

    public Customer join(String username, String password, String name, String email) {
        Customer customer = Customer.builder()
                .username(username)
                .password(password)
                .apiKey(username)
                .name(name)
                .email(email)
                .build();

        return customerRepository.save(customer);
    }

    public long count() {
        return customerRepository.count();
    }

    public Optional<Customer> findByUsername(String username) {
        return customerRepository.findByUsername(username);
    }

    public Optional<Customer> findByApiKey(String apiKey) {
        return customerRepository.findByApiKey(apiKey);
    }

    public Optional<Customer> getCustomerById(Long id) {
        return Optional.ofNullable(customerRepository.findById(id))
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }

    public List<Customer> getAllCustomers() {

        return customerRepository.findAll();
    }

    public Customer updateCustomer(Long id, CustomerDto customerDto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        customer.setName(customerDto.getName());
        customer.setEmail(customerDto.getEmail());
        return customer;
    }

    public Customer register(Customer customer) {
        // 회원 DB 저장
        Customer savedCustomer = customerRepository.save(customer);

        // 이메일 발송 예시
        String subject = "[Team4] 회원가입을 환영합니다!";
        String text = "안녕하세요, " + savedCustomer.getName() + " 님!\n"
                + "Team4 서비스를 이용해주셔서 감사합니다.\n"
                + "더 좋은 서비스로 보답하겠습니다.";

        emailService.sendSimpleEmail(savedCustomer.getEmail(), subject, text);

        return savedCustomer;
    }
}
