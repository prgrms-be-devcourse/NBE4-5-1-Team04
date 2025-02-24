package com.team4.project1.global.init;

import com.team4.project1.domain.customer.entity.Customer;
import com.team4.project1.domain.customer.service.CustomerService;
import com.team4.project1.domain.item.service.ItemService;
import com.team4.project1.domain.order.dto.OrderItemDto;
import com.team4.project1.domain.order.dto.OrderWithOrderItemsDto;
import com.team4.project1.domain.order.repository.OrderRepository;
import com.team4.project1.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class BaseInitData {
    private final OrderService orderService;
    private final CustomerService customerService;
    private final ItemService itemService;
    private final OrderRepository orderRepository;

    @Autowired
    @Lazy
    private BaseInitData self;

    @Bean
    public ApplicationRunner applicationRunner() {
        return args -> {
            self.customerInit();
            self.itemInit();
            self.orderInit();
        };
    }

    @Transactional
    public void customerInit() {
        if (customerService.count() > 0) {
            return;
        }

        customerService.join("jjang9", "jjang1234", "짱구", "jjang9@example.com");
        customerService.join("cheolsu", "cheolsu1234", "철수", "cheolsu@example.com");
        customerService.join("yuli", "yuli1234", "유리", "yuli@example.com");
        customerService.join("maeng9", "maeng1234", "맹구", "maeng9@example.com");
    }

    @Transactional
    public void itemInit() {
        if (itemService.count() > 0) {
            return;
        }

        itemService.addItem("스타벅스커피",48000,10);
        itemService.addItem("믹스커피",1000,10);
        itemService.addItem("공유커피",2500,10);
        itemService.addItem("컴포즈커피",38000,10);
    }

    @Transactional
    public void orderInit() {
        if (orderRepository.count() > 0) {
            return;
        }

        // ✅ ID 1번 고객 주문 생성
        Optional<Customer> customerOpt1 = customerService.getCustomerById(1L);
        if (customerOpt1.isPresent()) {
            Customer customer1 = customerOpt1.get();
            List<OrderItemDto> orderItemDtos1 = new ArrayList<>();
            orderItemDtos1.add(new OrderItemDto(1L, 3));
            orderItemDtos1.add(new OrderItemDto(2L, 2));

            OrderWithOrderItemsDto createdOrder1 = orderService.createOrder(orderItemDtos1, customer1.getId());
            System.out.println("생성된 주문 (ID 1): " + createdOrder1.getId() + ", 총 가격: " + createdOrder1.getTotalPrice());
        }

        // ✅ ID 2번 고객 주문 생성
        Optional<Customer> customerOpt2 = customerService.getCustomerById(2L);
        if (customerOpt2.isPresent()) {
            Customer customer2 = customerOpt2.get();
            List<OrderItemDto> orderItemDtos2 = new ArrayList<>();
            orderItemDtos2.add(new OrderItemDto(3L, 1));  // 3번 상품 1개
            orderItemDtos2.add(new OrderItemDto(4L, 2));  // 4번 상품 2개

            OrderWithOrderItemsDto createdOrder2 = orderService.createOrder(orderItemDtos2, customer2.getId());
            System.out.println("생성된 주문 (ID 2): " + createdOrder2.getId() + ", 총 가격: " + createdOrder2.getTotalPrice());
        }
    }

}
