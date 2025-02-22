package com.team4.project1.domain.order.Controller;

import com.team4.project1.domain.order.dto.OrderDto;
import com.team4.project1.domain.order.dto.OrderItemDto;
import com.team4.project1.domain.order.dto.OrderWithOrderItemsDto;
import com.team4.project1.domain.order.entity.Order;
import com.team4.project1.domain.order.entity.OrderItem;
import com.team4.project1.domain.order.service.OrderItemService;
import com.team4.project1.global.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/order-items")
@RequiredArgsConstructor
public class ApiV1OrderItemController {

    private final OrderItemService orderItemService;

    /**
     * 특정 주문(Order)의 OrderItem 목록 조회
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<List<OrderItemDto>> getOrderItemsByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderItemService.getOrderItemsByOrderId(orderId));
    }

    /**
     * 특정 OrderItem 조회
     */
    @GetMapping("/item/{id}")
    public ResponseEntity<OrderItemDto> getOrderItemById(@PathVariable Long id) {
        Optional<OrderItemDto> orderItemDto = orderItemService.getOrderItemById(id);

        return orderItemDto.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }




}
