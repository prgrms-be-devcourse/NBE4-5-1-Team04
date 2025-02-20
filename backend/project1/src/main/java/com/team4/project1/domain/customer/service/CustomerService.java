package com.team4.project1.domain.customer.service;

import com.team4.project1.domain.customer.entity.Customer;
import com.team4.project1.domain.customer.repository.CustomerRepository;
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

    // customer 객체 생성
    public Customer join(String username, String password, String name, String email) {
        Customer customer = Customer.builder()
                .username(username)
                .password(password)
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

    // Cusotmers id 기반 조회
    public Optional<Customer> getcustomerById(Long id) {
        return customerRepository.findById(id);
    }

    // customer 전체 조회
    public List<Customer> getAllcustomer() {
        return customerRepository.findAll();
    }

}
