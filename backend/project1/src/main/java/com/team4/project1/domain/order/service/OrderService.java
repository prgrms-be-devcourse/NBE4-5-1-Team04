package com.team4.project1.domain.order.service;

import com.team4.project1.domain.customer.entity.Customer;
import com.team4.project1.domain.customer.service.CustomerService;
import com.team4.project1.domain.item.entity.Item;
import com.team4.project1.domain.item.repository.ItemRepository;
import com.team4.project1.domain.order.dto.OrderItemDto;
import com.team4.project1.domain.order.dto.OrderWithOrderItemsDto;
import com.team4.project1.domain.order.entity.Order;
import com.team4.project1.domain.order.entity.OrderItem;
import com.team4.project1.domain.order.repository.OrderItemRepository;
import com.team4.project1.domain.order.repository.OrderRepository;
import com.team4.project1.global.exception.CustomerNotFoundException;
import com.team4.project1.global.exception.InsufficientStockException;
import com.team4.project1.global.exception.ItemNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ItemRepository itemRepository;
    private final CustomerService customerService;

    public OrderWithOrderItemsDto createOrder(List<OrderItemDto> orderItemDtos, Long customerId) {
        Customer customer = customerService.getCustomerById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        Order newOrder = new Order(customer, java.time.LocalDateTime.now(), 0L);
        orderItemDtos = validateNewOrder(orderItemDtos);
        long totalPrice = 0L;

        for (OrderItemDto orderItemDto : orderItemDtos) {
            // 재고 체크
            checkStockAvailability(orderItemDto.getItemId(), orderItemDto.getQuantity());

            OrderItem newOrderItem = new OrderItem(
                    newOrder,
                    itemRepository.getReferenceById(orderItemDto.getItemId()),
                    orderItemDto.getQuantity()
            );
            newOrder.getOrderItems().add(newOrderItem);
            totalPrice += (long) newOrderItem.getItem().getPrice() * newOrderItem.getQuantity();
            orderItemRepository.save(newOrderItem);
        }
        newOrder.setTotalPrice(totalPrice);
        orderRepository.save(newOrder);
        return OrderWithOrderItemsDto.from(newOrder);
    }

    public OrderWithOrderItemsDto updateOrder(List<OrderItemDto> orderItemDtos, Long orderId) {
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다. (ID: " + orderId + ")"));

        orderItemDtos = validateUpdatedOrder(orderItemDtos, existingOrder);
        existingOrder.getOrderItems().clear();
        orderRepository.save(existingOrder);

        long totalPrice = 0L;

        for (OrderItemDto orderItemDto : orderItemDtos) {
            // 재고 체크
            checkStockAvailability(orderItemDto.getItemId(), orderItemDto.getQuantity());

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

    // 재고 확인 메서드 추가
    private void checkStockAvailability(Long itemId, Integer requestedQuantity) {
        // 해당 상품의 재고를 조회
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));

        // 재고가 부족한 경우 예외 발생
        if (item.getStock() < requestedQuantity) {
            throw new InsufficientStockException(itemId);
        }
    }

    private List<OrderItemDto> validateNewOrder(List<OrderItemDto> orderItemDtos) {
        return orderItemDtos.stream()
                .filter(dto -> itemRepository.existsById(dto.getItemId()))
                .toList();
    }

    private List<OrderItemDto> validateUpdatedOrder(List<OrderItemDto> orderItemDtos, Order existingOrder) {
        return orderItemDtos.stream()
                .filter(dto -> existingOrder.getOrderItems().stream()
                        .anyMatch(orderItem -> orderItem.getItem().getId().equals(dto.getItemId())))
                .toList();
    }

    public Long cancelOrder(Long orderId) {
        orderRepository.deleteById(orderId);
        return orderId;
    }
}
