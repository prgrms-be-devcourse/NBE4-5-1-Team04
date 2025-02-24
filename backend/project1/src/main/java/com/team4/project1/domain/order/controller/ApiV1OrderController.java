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

/**
 * 주문 관련 APi를 처리하는 컨트롤러입니다.
 * 주문 생성, 수정 취소 , 조회 기능을 제공합니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class ApiV1OrderController {

    private final OrderService orderService;

    /**
     * 새로운 주문을 생성하는 메서드입니다.
     * 사용자가 선택한 상품들로 주문을 생성, 해당 주문을 반환합니다.
     * @param orderItemDtos 주문에 포함될 상품들의 DTO목록
     * @param principal 현재 로그인한 사용자의 정보(주문을 생성한 사용자를 나타냄)
     * @return 생성된 주문과 주문 항목들의 DTO를 포함하는 응답을 반환합니다.
     */
    @PostMapping
    public ResponseEntity<ResponseDto<OrderWithOrderItemsDto>> createOrder(
            @RequestBody List<OrderItemDto> orderItemDtos,
            Principal principal) {
        return ResponseEntity.ok(ResponseDto.ok(orderService.createOrder(orderItemDtos, principal)));
    }

    /**
     * 기존 주문을 수정하는 메서드입니다.
     * 주어진 주문 ID와 수정된 상품 정보를 바탕으로 주문을 업데이트 합니다
     * @param orderId 수정할 주문의 ID
     * @param orderItemDtos 수정할 주문 상품의 DTO목록
     * @param principal 현재 로그인한 사용자의 정보 (주문을 수정한 사용자를 나타냄)
     * @return 수정된 주문과 주문 항목들의 DTO를 포함하는 응답을 반환합니다.
     */
    @PutMapping("/{orderId}")
    public ResponseEntity<ResponseDto<OrderWithOrderItemsDto>> updateOrder(
            @PathVariable Long orderId,
            @RequestBody List<OrderItemDto> orderItemDtos,
            Principal principal) {
        return ResponseEntity.ok(ResponseDto.ok(orderService.updateOrder(orderItemDtos, orderId, principal)));
    }

    /**
     * 주문을 취소하는 메서드입니다.
     * 주어진 주문 ID를 바탕으로 주문을 취소하고, 취소된 주문의 ID를 반환합니다.
     * @param orderId 취소할 주문의 ID
     * @param principal 현재 로그인한 사용자의 정보 (주문을 최소한 사용자를 나타냄)
     * @return 취소된 주문의 ID를 포함하는 응답을 반환합니다.
     */
    @DeleteMapping("/{orderId}")
    public ResponseEntity<ResponseDto<Long>> cancelOrder(
            @PathVariable Long orderId,
            Principal principal) {
        return ResponseEntity.ok(ResponseDto.ok(orderService.cancelOrder(orderId, principal)));
    }


    /**
     * 주문 ID로 특정 주문을 조회하는 메서드입니다.
     * 주어진 주문 ID를 바탕으로 주문과 주문 항목들의 정보를 조회하여 반환합니다.
     * @param orderId 조회할 주문의 ID
     * @param principal 현재 로그인한 사용자의 정보( 주문을 조회한 사용자를 나타냄)
     * @return 조회된 주문과 주문 항목들의 DTO를 포함하는 응답을 반환합니다.
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<ResponseDto<OrderWithOrderItemsDto>> getOrderByOrderId(
            @PathVariable Long orderId,
            Principal principal) {
        return ResponseEntity.ok(ResponseDto.ok(orderService.getOrderById(orderId, principal)));
    }

    /**
     * 현재 로그인한 사용자의 모든 주문을 조회하는 메서드입니다.
     * 로그인된 사용자의 모든 주문을 조회하여 목록을 반환합니다.
     * @param principal 현재 로그인한 사용자의 정보
     * @return 사용자의 모든 주문의 DTO 목록을 포함하는 응답을 반환합니다.
     */
    @GetMapping("")
    public ResponseEntity<ResponseDto<List<OrderDto>>> getOrdersByPrincipal(Principal principal) {
        List<OrderDto> orders = orderService.getOrdersByPrincipal(principal);
        return ResponseEntity.ok(ResponseDto.ok(orders));
    }
}
