package com.team4.project1.domain.order.service;

import com.team4.project1.domain.item.entity.Item;
import com.team4.project1.domain.item.repository.ItemRepository;
import com.team4.project1.domain.order.dto.OrderItemDto;
import com.team4.project1.domain.order.entity.Order;
import com.team4.project1.domain.order.entity.OrderItem;
import com.team4.project1.domain.order.repository.OrderItemRepository;
import com.team4.project1.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;

    public OrderItem createOrderItem(OrderItemDto orderItemDto) {
        Optional<Order> order = orderRepository.findById(orderItemDto.getOrderId());
        Optional<Item> item = itemRepository.findById(orderItemDto.getItemId());

        if (order.isPresent() && item.isPresent()) {
            OrderItem orderItem = OrderItem.builder()
                    .order(order.get())
                    .item(item.get())
                    .quantity(orderItemDto.getQuantity())
                    .build();

            return orderItemRepository.save(orderItem);
        } else {
            throw new IllegalArgumentException("Invalid order or item ID.");
        }
    }
}

