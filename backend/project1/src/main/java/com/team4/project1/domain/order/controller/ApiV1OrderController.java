package com.team4.project1.domain.order.Controller;

import com.team4.project1.domain.order.dto.OrderItemDto;
import com.team4.project1.domain.order.dto.OrderWithOrderItemsDto;
import com.team4.project1.domain.order.service.OrderItemService;
import com.team4.project1.domain.order.service.OrderService;
import com.team4.project1.global.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

}
