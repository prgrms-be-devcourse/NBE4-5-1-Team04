package com.team4.project1.domain.order.service;

import com.team4.project1.domain.customer.entity.Customer;
import com.team4.project1.domain.customer.service.CustomerService;
import com.team4.project1.domain.item.entity.Item;
import com.team4.project1.domain.item.service.ItemService;
import com.team4.project1.domain.order.dto.OrderItemDto;
import com.team4.project1.domain.order.dto.OrderWithOrderItemsDto;
import com.team4.project1.domain.order.entity.DeliveryStatus;
import com.team4.project1.domain.order.entity.Order;
import com.team4.project1.domain.order.entity.OrderItem;
import com.team4.project1.domain.order.repository.OrderItemRepository;
import com.team4.project1.domain.order.repository.OrderRepository;
import com.team4.project1.global.exception.ItemNotFoundException;
import com.team4.project1.global.exception.UnauthorizedAccessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ItemService itemService;
    private final CustomerService customerService;

    public OrderWithOrderItemsDto createOrder(List<OrderItemDto> orderItemDtos, String username) {
        Customer customer = customerService.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedAccessException("사용자를 찾을 수 없습니다. (Username: " + username + ")"));

        Order newOrder = new Order(customer, LocalDateTime.now(), 0L);
        long totalPrice = 0L;

        for (OrderItemDto orderItemDto : orderItemDtos) {
            Item item = Item.fromDto(itemService.getItemById(orderItemDto.getItemId())
                    .orElseThrow(() -> new ItemNotFoundException(orderItemDto.getItemId())));

            itemService.reduceStock(orderItemDto.getItemId(), orderItemDto.getQuantity());

            OrderItem newOrderItem = new OrderItem(newOrder, item, orderItemDto.getQuantity());
            newOrder.getOrderItems().add(newOrderItem);
            totalPrice += (long) newOrderItem.getItem().getPrice() * newOrderItem.getQuantity();
            orderItemRepository.save(newOrderItem);
        }

        newOrder.setTotalPrice(totalPrice);
        orderRepository.save(newOrder);
        return OrderWithOrderItemsDto.from(newOrder);
    }

    public Optional<OrderWithOrderItemsDto> updateOrder(List<OrderItemDto> orderItemDtos, Long orderId, Principal principal) {
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다. (ID: " + orderId + ")"));

        // 로그인한 사용자의 주문인지 확인
        String loggedInUsername = principal.getName();
        if (!existingOrder.getCustomer().getUsername().equals(loggedInUsername)) {
            throw new UnauthorizedAccessException("이 주문을 수정할 권한이 없습니다.");
        }

        // 기존 주문 아이템 제거
        existingOrder.getOrderItems().clear();
        orderRepository.save(existingOrder);

        long totalPrice = 0L;

        for (OrderItemDto orderItemDto : orderItemDtos) {
            Item item = itemService.getItemById(orderItemDto.getItemId())
                    .map(Item::fromDto)
                    .orElseThrow(() -> new ItemNotFoundException(orderItemDto.getItemId()));

            itemService.reduceStock(orderItemDto.getItemId(), orderItemDto.getQuantity());

            OrderItem newOrderItem = new OrderItem(existingOrder, item, orderItemDto.getQuantity());
            existingOrder.getOrderItems().add(newOrderItem);
            totalPrice += (long) newOrderItem.getItem().getPrice() * newOrderItem.getQuantity();
            orderItemRepository.save(newOrderItem);
        }

        existingOrder.setDate(LocalDateTime.now());
        existingOrder.setTotalPrice(totalPrice);
        orderRepository.save(existingOrder);

        return Optional.of(OrderWithOrderItemsDto.from(existingOrder));
    }

    // 주문 취소 메소드
    public Long cancelOrder(Long orderId) {
        orderRepository.deleteById(orderId);
        return orderId;
    }

    public List<OrderWithOrderItemsDto> getOrdersByCustomerId(Long customerId) {
        List<Order> orders = orderRepository.findAllByCustomerId(customerId);

        // 주문을 조회할 때 최신 배송 상태 반영
        orders.forEach(this::updateOrderStatusOnFetch);


        return orders.stream()
                .map(OrderWithOrderItemsDto::from)
                .toList();
    }
  
    // 새 주문 검증
    private List<OrderItemDto> validateNewOrder(List<OrderItemDto> orderItemDtos) {
        return orderItemDtos.stream()
                .filter(dto -> itemService.getItemById(dto.getItemId()).isPresent())  // 아이템 존재 여부 검증
                .toList();
    }


    // 주문 수정 검증
    private List<OrderItemDto> validateUpdatedOrder(List<OrderItemDto> orderItemDtos, Order existingOrder) {
        return orderItemDtos.stream()
                .filter(dto -> existingOrder.getOrderItems().stream()
                        .anyMatch(orderItem -> orderItem.getItem().getId().equals(dto.getItemId())))  // 아이템 존재 여부 검증
                .toList();
    }


    public Optional<OrderWithOrderItemsDto> getOrderById(Long orderId, Principal principal) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다. (ID: " + orderId + ")"));

        // 현재 로그인한 사용자와 주문의 생성자가 일치하는지 확인
        String loggedInUsername = principal.getName();

        if (!order.getCustomer().getUsername().equals(loggedInUsername)) {
            throw new UnauthorizedAccessException("이 주문을 열람할 권한이 없습니다.");
        }

        // 상태 업데이트 처리
        updateOrderStatusOnFetch(order);

        return Optional.of(OrderWithOrderItemsDto.from(order));
    }

    private void updateOrderStatusOnFetch(Order order) {
        LocalDateTime today2PM = LocalDateTime.now().withHour(14).withMinute(0).withSecond(0).withNano(0);

        if (order.getDate().isBefore(today2PM)) {
            order.setDeliveryStatus(DeliveryStatus.SHIPPED); // 오후 2시 이전 주문 → 배송됨
        } else {
            order.setDeliveryStatus(DeliveryStatus.PROCESSING); // 오후 2시 이후 주문 → 배송 준비 중
        }
        orderRepository.save(order);
    }
}
