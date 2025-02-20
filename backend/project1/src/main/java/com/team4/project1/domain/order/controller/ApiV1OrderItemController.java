package com.team4.project1.domain.order.controller;

import com.team4.project1.domain.order.dto.OrderItemDto;
import com.team4.project1.domain.order.entity.OrderItem;
import com.team4.project1.domain.order.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/order-items")
@RequiredArgsConstructor
public class ApiV1OrderItemController {

    private final OrderItemService orderItemService;

    // 모든 OrderItem 조회
    @GetMapping
    public ResponseEntity<List<OrderItem>> getAllOrderItems() {
        List<OrderItem> orderItems = orderItemService.getAllOrderItems();
        return ResponseEntity.ok(orderItems);
    }

    // 특정 OrderItem 조회
    @GetMapping("/{orderItemId}")
    public ResponseEntity<OrderItem> getOrderItemById(@PathVariable Long orderItemId) {
        return orderItemService.getOrderItemById(orderItemId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // OrderItem 생성
    @PostMapping
    public ResponseEntity<OrderItem> createOrderItem(@RequestBody OrderItemDto orderItemDto) {
        OrderItem orderItem = orderItemService.createOrderItem(orderItemDto);
        return ResponseEntity.ok(orderItem);
    }
}
