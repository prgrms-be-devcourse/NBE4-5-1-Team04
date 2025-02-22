package com.team4.project1.domain.order.controller;

import com.team4.project1.domain.order.dto.OrderItemDto;
import com.team4.project1.domain.order.dto.OrderWithOrderItemsDto;
import com.team4.project1.domain.order.service.OrderItemService;
import com.team4.project1.domain.order.service.OrderService;
import com.team4.project1.global.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
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
    private final OrderItemService orderItemService;

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
    @GetMapping(value = "", params = "cust_id")
    public ResponseEntity<ResponseDto<List<OrderWithOrderItemsDto>>> getOrdersByCustomerId(
            @RequestParam("cust_id") Long customerId) {
        List<OrderWithOrderItemsDto> orders = orderService.getOrdersByCustomerId(customerId);
        return ResponseEntity.ok(ResponseDto.ok(orders));
    }
    @GetMapping("/{orderId}")
    public ResponseEntity<ResponseDto<OrderWithOrderItemsDto>> getOrderById(@PathVariable Long orderId) {
        OrderWithOrderItemsDto order = orderService.getOrderById(orderId)
                .orElseThrow(() -> new RuntimeException("해당 주문을 찾을 수 없습니다. (ID: " + orderId + ")"));

        return ResponseEntity.ok(ResponseDto.ok(order));
    }

}
