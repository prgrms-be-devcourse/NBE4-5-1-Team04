package com.team4.project1.domain.customers.service;

import com.team4.project1.domain.customers.repository.CustomersRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomersService {


    private CustomersRepository customersRepository;

    public CustomersService(CustomersRepository customersRepository) {
        this.customersRepository = customersRepository;
    }

    public

    // 회원정보 조회
    @Transactional
    public void checkinfo(Long id) {

    }

}
