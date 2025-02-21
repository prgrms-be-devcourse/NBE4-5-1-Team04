package com.team4.project1.domain.order.service;

import com.team4.project1.domain.item.repository.ItemRepository;
import com.team4.project1.domain.order.dto.OrderItemDto;
import com.team4.project1.domain.order.dto.OrderWithOrderItemsDto;
import com.team4.project1.domain.order.entity.Order;
import com.team4.project1.domain.order.entity.OrderItem;
import com.team4.project1.domain.order.repository.OrderItemRepository;
import com.team4.project1.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    public final OrderRepository orderRepository;
    public final OrderItemRepository orderItemRepository;
    public final ItemRepository itemRepository;

    // TODO: 작업 성공/실패 여부를 어떤 형식으로든 반환하게끔 개선
    public OrderWithOrderItemsDto createOrder(List<OrderItemDto> orderItemDtos) {
        log.info("Creating new order...");

        Order newOrder = new Order();
        long totalPrice = 0L;

        for (OrderItemDto orderItemDto : orderItemDtos) {
            OrderItem newOrderItem = new OrderItem(
                    newOrder,
                    itemRepository.findById(orderItemDto.getItemId())
                            .orElseThrow(() -> new RuntimeException("Item not found: " + orderItemDto.getItemId())),
                    orderItemDto.getQuantity()
            );
            newOrder.getOrderItems().add(newOrderItem);
            totalPrice += newOrderItem.getItem().getPrice() * newOrderItem.getQuantity();
        }

        newOrder.setTotalPrice(totalPrice);
        orderRepository.save(newOrder);

        log.info("Order created successfully, orderId={}", newOrder.getId());
        return OrderWithOrderItemsDto.from(newOrder);
    }

    // TODO: 작업 성공/실패 여부를 어떤 형식으로든 반환하게끔 개선
    public OrderWithOrderItemsDto updateOrder(List<OrderItemDto> orderItemDtos, Long orderId) {
        // TODO: orderId에 해당하는 Order 엔티티가 없을 때 던지는 예외를 커스텀 구현체로 변경
        Order existingOrder = orderRepository.findById(orderId).orElseThrow();
        orderItemDtos = validateUpdatedOrder(orderItemDtos, existingOrder);
        // TODO: Order.orderItems에 걸려있는 orphanRemoval로 모든 주문 내역을 삭제하는 대신, 수정해야하는 내역만 수정(UPDATE문)하도록 개선
        existingOrder.getOrderItems().clear();
        orderRepository.save(existingOrder);
        long totalPrice = 0L;
        for(OrderItemDto orderItemDto : orderItemDtos) {
            OrderItem newOrderItem = new OrderItem(
                    existingOrder,
                    itemRepository.getReferenceById(orderItemDto.getItemId()),
                    orderItemDto.getQuantity()
            );
            existingOrder.getOrderItems().add(newOrderItem);
            totalPrice += (long) newOrderItem.getItem().getPrice() * newOrderItem.getQuantity();
            orderItemRepository.save(newOrderItem);
        }
        existingOrder.setDate(java.time.LocalDateTime.now());
        existingOrder.setTotalPrice(totalPrice);
        orderRepository.save(existingOrder);
        return OrderWithOrderItemsDto.from(existingOrder);
    }

    public Long cancelOrder(Long orderId) {
        orderRepository.deleteById(orderId);
        return orderId;
    }

    private List<OrderItemDto> validateNewOrder(List<OrderItemDto> orderItemDtos) {
        // 새 주문을 받았을때, 주문 가능한 제품만 남긴다.
        return orderItemDtos.stream()
                .filter(dto -> itemRepository.existsById(dto.getItemId()))
                .toList();
    }

    private List<OrderItemDto> validateUpdatedOrder(List<OrderItemDto> orderItemDtos, Order existingOrder) {
        // 기존 주문에 대한 수정 요청을 받았을때, 기존 주문 내역에 존재하는 제품만 남긴다.
        return orderItemDtos.stream()
                .filter(dto -> existingOrder.getOrderItems().stream()
                        .anyMatch(orderItem -> orderItem.getItem().getId().equals(dto.getItemId())))
                .toList();
    }
}
