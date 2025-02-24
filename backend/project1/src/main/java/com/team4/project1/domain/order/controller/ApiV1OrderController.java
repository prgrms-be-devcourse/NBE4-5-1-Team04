package com.team4.project1.domain.order.Controller;

import com.team4.project1.domain.order.dto.OrderDto;
import com.team4.project1.domain.order.dto.OrderItemDto;
import com.team4.project1.domain.order.dto.OrderWithOrderItemsDto;
import com.team4.project1.domain.order.service.OrderService;
import com.team4.project1.global.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class ApiV1OrderController {

    private final OrderService orderService;


    @PostMapping
    public ResponseEntity<ResponseDto<OrderWithOrderItemsDto>> createOrder(
            @RequestBody List<OrderItemDto> orderItemDtos,
            Principal principal) {
        return ResponseEntity.ok(ResponseDto.ok(orderService.createOrder(orderItemDtos, principal)));
    }


    @PutMapping("/{orderId}")
    public ResponseEntity<ResponseDto<OrderWithOrderItemsDto>> updateOrder(
            @PathVariable Long orderId,
            @RequestBody List<OrderItemDto> orderItemDtos,
            Principal principal) {
        return ResponseEntity.ok(ResponseDto.ok(orderService.updateOrder(orderItemDtos, orderId, principal)));
    }


    @DeleteMapping("/{orderId}")
    public ResponseEntity<ResponseDto<Long>> cancelOrder(
            @PathVariable Long orderId,
            Principal principal) {
        return ResponseEntity.ok(ResponseDto.ok(orderService.cancelOrder(orderId, principal)));
    }


    @GetMapping("/{orderId}")
    public ResponseEntity<ResponseDto<OrderWithOrderItemsDto>> getOrderByOrderId(
            @PathVariable Long orderId,
            Principal principal) {
        return ResponseEntity.ok(ResponseDto.ok(orderService.getOrderById(orderId, principal)));
    }


    @GetMapping("")
    public ResponseEntity<ResponseDto<List<OrderDto>>> getOrdersByPrincipal(Principal principal) {
        List<OrderDto> orders = orderService.getOrdersByPrincipal(principal);
        return ResponseEntity.ok(ResponseDto.ok(orders));
    }
}
