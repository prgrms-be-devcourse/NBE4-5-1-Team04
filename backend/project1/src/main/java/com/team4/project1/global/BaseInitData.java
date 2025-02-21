package com.team4.project1.global;

import com.team4.project1.domain.customer.entity.Customer;
import com.team4.project1.domain.customer.service.CustomerService;
import com.team4.project1.domain.item.service.ItemService;
import com.team4.project1.domain.order.controller.ApiV1OrderController;
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

        itemService.addItem("스타벅스커피",48000);
        itemService.addItem("믹스커피",1000);
        itemService.addItem("공유커피",2500);
        itemService.addItem("컴포즈커피",38000);
    }
    @Transactional
    public void orderInit() {
        if (orderRepository.count() > 0) {
            return;
        }
        // 임의로 고객 id 1번을 사용 (이미 customerInit에서 데이터가 생성되었음)
        Optional<Customer> customerOpt = customerService.getCustomerById(1L);
        if (customerOpt.isEmpty()) {
            return;
        }
        Customer customer = customerOpt.get();

        // 주문 항목 데이터 생성: 예를 들어, item id 1번 상품을 3개, item id 2번 상품을 2개 주문
        List<OrderItemDto> orderItemDtos = new ArrayList<>();
        orderItemDtos.add(new OrderItemDto(1L, 3));
        orderItemDtos.add(new OrderItemDto(2L, 2));

        // OrderService의 createOrder() 메서드를 호출하여 주문 생성
        OrderWithOrderItemsDto createdOrder = orderService.createOrder(orderItemDtos, customer);
        System.out.println("생성된 주문 ID: " + createdOrder.getId() + ", 총 가격: " + createdOrder.getTotalPrice());
    }




}
