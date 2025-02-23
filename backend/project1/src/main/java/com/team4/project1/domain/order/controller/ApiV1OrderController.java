package com.team4.project1.domain.order.Controller;

import com.team4.project1.domain.order.dto.OrderDto;
import com.team4.project1.domain.order.dto.OrderItemDto;
import com.team4.project1.domain.order.dto.OrderWithOrderItemsDto;
import com.team4.project1.domain.order.entity.Order;
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

    @GetMapping("/{orderId}")
    public ResponseEntity<ResponseDto<OrderWithOrderItemsDto>> getOrderByOrderId(
            @PathVariable Long orderId) {
        // TODO: 서비스 레이어에서 예외를 던지도록 수정
        OrderWithOrderItemsDto order = orderService.getOrderById(orderId)
                .orElseThrow(() -> new RuntimeException("해당 주문을 찾을 수 없습니다. (ID: " + orderId + ")"));
        return ResponseEntity.ok(ResponseDto.ok(orderService.getOrderWithItems(orderId)));
    }

    @GetMapping(value = "", params = "cust_id")
    public ResponseEntity<ResponseDto<List<OrderDto>>> getAllOrders(
            @RequestParam("cust_id") Long customerId) {
        return ResponseEntity.ok(ResponseDto.ok(orderService.getOrdersByCustomerId(customerId)));
    }
}
