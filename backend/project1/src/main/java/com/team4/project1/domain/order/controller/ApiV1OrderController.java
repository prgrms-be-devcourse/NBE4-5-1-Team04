package com.team4.project1.domain.order.controller;

import com.team4.project1.domain.order.dto.OrderDto;
import com.team4.project1.domain.order.dto.OrderItemDto;
import com.team4.project1.domain.order.dto.OrderWithOrderItemsDto;
import com.team4.project1.domain.order.entity.Order;
import com.team4.project1.domain.order.service.OrderService;
import com.team4.project1.global.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class ApiV1OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ResponseDto<OrderWithOrderItemsDto>> createOrder(
            @RequestParam("cust_id") Long customerId,
            @RequestBody List<OrderItemDto> orderItemDtos) {
        return ResponseEntity.ok(ResponseDto.ok(orderService.createOrder(orderItemDtos, customerId)));
    }


    @PutMapping("/{orderId}")
    public ResponseEntity<ResponseDto<OrderWithOrderItemsDto>> updateOrder(@PathVariable Long orderId, @RequestBody List<OrderItemDto> orderItemDtos) {
        return ResponseEntity.ok(ResponseDto.ok(orderService.updateOrder(orderItemDtos, orderId)));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<ResponseDto<Long>> cancelOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(ResponseDto.ok(orderService.cancelOrder(orderId)));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ResponseDto<OrderWithOrderItemsDto>> getOrderByOrderId(
            @PathVariable Long orderId) {
        return ResponseEntity.ok(ResponseDto.ok(orderService.getOrderById(orderId)));
    }

    @GetMapping("")
    public ResponseEntity<ResponseDto<Page<OrderWithOrderItemsDto>>> getAllOrders(
            @RequestParam("cust_id") Long customerId,
            Pageable pageable) {
        Page<OrderWithOrderItemsDto> orders = orderService.getOrdersByCustomerId(customerId, pageable);
        return ResponseEntity.ok(ResponseDto.ok(orders));
    }
}
