package com.team4.project1.global;

import com.team4.project1.domain.customer.dto.CustomerDto;
import com.team4.project1.domain.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
public class BaseInitData {

    private final CustomerService customerService;

    @Bean
    public ApplicationRunner applicationRunner() {
        return args -> customerInit();
    }

    @Transactional
    public void customerInit() {
        if (customerService.getAllcustomer().size() > 0) {
            return;
        }

        customerService.createCustomer(new CustomerDto("짱구", "jjang9@example.com"));
        customerService.createCustomer(new CustomerDto("철수", "cheolsu@example.com"));
        customerService.createCustomer(new CustomerDto("유리", "yuli@example.com"));
        customerService.createCustomer(new CustomerDto("맹구", "maeng9@example.com"));
    }
}
