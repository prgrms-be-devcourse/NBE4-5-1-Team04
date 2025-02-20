package com.team4.project1.domain.order.service;

import com.team4.project1.domain.order.dto.OrderItemDto;
import com.team4.project1.domain.order.entity.OrderItem;
import com.team4.project1.domain.order.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    /**
     * 특정 주문(Order) ID에 해당하는 OrderItem 목록 조회
     */
    public List<OrderItemDto> getOrderItemsByOrderId(Long orderId) {
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);

        return orderItems.stream()
                .map(OrderItemDto::from)  // OrderItemDto.from() 메서드를 활용하여 변환
                .collect(Collectors.toList());
    }

    /**
     * 특정 OrderItem ID로 조회 (개별 조회)
     */
    public Optional<OrderItemDto> getOrderItemById(Long id) {
        return orderItemRepository.findById(id)
                .map(OrderItemDto::from);
    }
}
