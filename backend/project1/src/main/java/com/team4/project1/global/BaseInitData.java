package com.team4.project1.global;

import com.team4.project1.domain.customer.dto.CustomerDto;
import com.team4.project1.domain.customer.service.CustomerService;
import com.team4.project1.domain.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
public class BaseInitData {

    private final CustomerService customerService;
    private final ItemService itemService;

    @Autowired
    @Lazy
    private BaseInitData self;

    @Bean
    public ApplicationRunner applicationRunner() {
        return args -> {
            self.customerInit();
            self.itemInit();
        };
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

    @Transactional
    public void itemInit() {
        if (itemService.count() > 0) {
            return;
        }

        itemService.addItem("스타벅스커피",48000);
        itemService.addItem("믹스커피",1000);
        itemService.addItem("공유커피",2500);
        itemService.addItem("컴포즈커피",38000);
    }
}
