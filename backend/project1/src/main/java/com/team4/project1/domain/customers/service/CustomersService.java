package com.team4.project1.domain.customers.service;

import com.team4.project1.domain.customers.dto.CustomersDto;
import com.team4.project1.domain.customers.entity.Customers;
import com.team4.project1.domain.customers.repository.CustomersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomersService {

    private CustomersRepository customersRepository;

    public CustomersService(CustomersRepository customersRepository) {
        this.customersRepository = customersRepository;
    }

    // Customers 객체 생성
    public Customers createCustomer(CustomersDto customersDto) {
        Customers customers = Customers.builder()
                .name(customersDto.getName())
                .email(customersDto.getEmail())
                .build();
        return customersRepository.save(customers);
    }

    // Cusotmers id 기반 조회
    public Optional<Customers> getCustomersById(Long id) {
        return customersRepository.findById(id);
    }

    // Customers 전체 조회
    public List<Customers> getAllCustomers() {
        return customersRepository.findAll();
    }

}
