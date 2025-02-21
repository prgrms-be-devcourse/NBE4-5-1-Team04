package com.team4.project1.domain.order.controller;

import com.team4.project1.domain.customer.entity.Customer;
import com.team4.project1.domain.customer.service.CustomerService;
import com.team4.project1.domain.order.dto.OrderItemDto;
import com.team4.project1.domain.order.dto.OrderWithOrderItemsDto;
import com.team4.project1.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class ApiV1OrderController {

    private final CustomerService customerService;
    private final OrderService orderService;

    @PostMapping()
    public OrderWithOrderItemsDto createOrder(@RequestParam("cust_id") Long customerId, @RequestBody List<OrderItemDto> orderItemDtos) {
        // TODO: customerId에 해당하는 Customer 엔티티가 없을 때 던지는 예외를 커스텀 구현체로 변경
        Optional<Customer> customer = customerService.getCustomerById(customerId);
        if(customer.isEmpty()) {
            throw new RuntimeException("Customer with id %d not found".formatted(customerId));
        }
        return orderService.createOrder(orderItemDtos, customer.get());
    }

    @PutMapping("/{orderId}")
    public OrderWithOrderItemsDto updateOrder(@PathVariable Long orderId, @RequestBody List<OrderItemDto> orderItemDtos) {
        return orderService.updateOrder(orderItemDtos, orderId);
    }

    @DeleteMapping("/{orderId}")
    public Long cancelOrder(@PathVariable Long orderId) {
        return orderService.cancelOrder(orderId);
    }
}
