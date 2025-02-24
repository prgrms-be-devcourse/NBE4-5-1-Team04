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

        itemService.addItem("스타벅스", 6800, 10);
        itemService.addItem("맥심커피", 1000, 10);
        itemService.addItem("카누커피", 1500, 10);
        itemService.addItem("컴포즈 커피", 2800, 10);
        itemService.addItem("이디야 커피", 3000, 10);
        itemService.addItem("빽다방", 2000, 10);
        itemService.addItem("커피빈", 4000, 10);
        itemService.addItem("투썸플레이스", 5000, 10);
        itemService.addItem("엔젤리너스", 3500, 10);
        itemService.addItem("더벤티", 4500, 10);
        itemService.addItem("탐앤탐스", 4200, 10);
        itemService.addItem("폴바셋", 5500, 10);
        itemService.addItem("할리스", 4000, 10);
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
        OrderWithOrderItemsDto createdOrder = orderService.createOrder(orderItemDtos, customer.getId());
        // TODO: stdout으로 바로 출력하는 대신 로깅(@Slf4j)으로 출력하게끔 변경을 고려
        System.out.println("생성된 주문 ID: " + createdOrder.getId() + ", 총 가격: " + createdOrder.getTotalPrice());
    }
}
