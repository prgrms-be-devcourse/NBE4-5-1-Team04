package com.team4.project1.domain.customer.service;

import com.team4.project1.domain.customer.dto.CustomerDto;
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

    private CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    // customer 객체 생성
    public Customer createCustomer(CustomerDto customerDto) {
        Customer customer = Customer.builder()
                .name(customerDto.getName())
                .email(customerDto.getEmail())
                .build();
        return customerRepository.save(customer);
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
