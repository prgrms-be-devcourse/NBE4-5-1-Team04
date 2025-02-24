package com.team4.project1.domain.order.service;

import com.team4.project1.domain.customer.entity.Customer;
import com.team4.project1.domain.customer.service.CustomerService;
import com.team4.project1.domain.item.dto.ItemDto;
import com.team4.project1.domain.item.entity.Item;
import com.team4.project1.domain.item.service.ItemService;
import com.team4.project1.domain.order.dto.OrderDto;
import com.team4.project1.domain.order.dto.OrderItemDto;
import com.team4.project1.domain.order.dto.OrderWithOrderItemsDto;
import com.team4.project1.domain.order.entity.DeliveryStatus;
import com.team4.project1.domain.order.entity.Order;
import com.team4.project1.domain.order.entity.OrderItem;
import com.team4.project1.domain.order.repository.OrderItemRepository;
import com.team4.project1.domain.order.repository.OrderRepository;
import com.team4.project1.global.exception.CustomerNotFoundException;
import com.team4.project1.global.exception.ItemNotFoundException;
import com.team4.project1.global.exception.UnauthorizedAccessException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ItemService itemService;
    private final CustomerService customerService;

    public OrderWithOrderItemsDto createOrder(List<OrderItemDto> orderItemDtos, Principal principal) {
        // 비로그인 체크
        if (principal == null) {
            throw new UnauthorizedAccessException("로그인 후 주문을 생성할 수 있습니다.");
        }

        String currentUsername = principal.getName();  // Principal에서 사용자 이름을 가져옵니다.
        Customer customer = customerService.findByUsername(currentUsername)
                .orElseThrow(() -> new CustomerNotFoundException("사용자를 찾을 수 없습니다."));

        Order newOrder = new Order(customer, LocalDateTime.now(), 0L);

        orderItemDtos = validateNewOrder(orderItemDtos);  // 주문 아이템 유효성 검사
        long totalPrice = 0L;

        for (OrderItemDto orderItemDto : orderItemDtos) {
            Item item = itemService.getItemById(orderItemDto.getItemId())
                    .map(ItemDto::toEntity)
                    .orElseThrow(() -> new ItemNotFoundException(orderItemDto.getItemId()));

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

    public OrderWithOrderItemsDto updateOrder(List<OrderItemDto> orderItemDtos, Long orderId, Principal principal) {
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다. (ID: " + orderId + ")"));

        if (principal == null) {
            throw new UnauthorizedAccessException("로그인 후 주문을 취소할 수 있습니다.");
        }

        String currentUsername = principal.getName();
        if (!existingOrder.getCustomer().getUsername().equals(currentUsername)) {
            throw new UnauthorizedAccessException("본인만 자신의 주문을 수정할 수 있습니다.");
        }

        updateOrderStatusOnFetch(existingOrder);
        if (existingOrder.getDeliveryStatus() == DeliveryStatus.SHIPPED) {
            throw new IllegalStateException("이미 발송된 주문은 수정할 수 없습니다.");
        }

        orderItemDtos = validateUpdatedOrder(orderItemDtos, existingOrder);  // 수정된 유효성 검사 호출
        existingOrder.getOrderItems().clear();

        long totalPrice = 0L;
        for (OrderItemDto orderItemDto : orderItemDtos) {
            Item item = itemService.getItemById(orderItemDto.getItemId())
                    .map(ItemDto::toEntity)
                    .orElseThrow(() -> new ItemNotFoundException(orderItemDto.getItemId()));

            itemService.reduceStock(orderItemDto.getItemId(), orderItemDto.getQuantity());  // 재고 감소 처리

            OrderItem newOrderItem = new OrderItem(existingOrder, item, orderItemDto.getQuantity());
            existingOrder.getOrderItems().add(newOrderItem);
            totalPrice += (long) newOrderItem.getItem().getPrice() * newOrderItem.getQuantity();
            orderItemRepository.save(newOrderItem);
        }

        existingOrder.setDate(LocalDateTime.now());
        existingOrder.setTotalPrice(totalPrice);
        orderRepository.save(existingOrder);
        return OrderWithOrderItemsDto.from(existingOrder);
    }

    public Long cancelOrder(Long orderId, Principal principal) {
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다. (ID: " + orderId + ")"));

        if (principal == null) {
            throw new UnauthorizedAccessException("로그인 후 주문을 취소할 수 있습니다.");
        }

        String currentUsername = principal.getName();
        if (!existingOrder.getCustomer().getUsername().equals(currentUsername)) {
            throw new UnauthorizedAccessException("본인만 자신의 주문을 취소할 수 있습니다.");
        }

        updateOrderStatusOnFetch(existingOrder);
        if (existingOrder.getDeliveryStatus() == DeliveryStatus.SHIPPED) {
            throw new IllegalStateException("이미 발송된 주문은 취소할 수 없습니다.");
        }

        orderRepository.deleteById(orderId);
        return orderId;
    }

    public Page<OrderDto> getOrdersByPrincipal(Principal principal, Pageable pageable) {
        if (principal == null) {
            throw new UnauthorizedAccessException("로그인 후 주문을 취소할 수 있습니다.");
        }

        String currentUsername = principal.getName();
        Customer customer = customerService.findByUsername(currentUsername)
                .orElseThrow(() -> new CustomerNotFoundException("사용자를 찾을 수 없습니다."));

        Page<Order> orders = orderRepository.findAllByCustomerId(customer.getId(), pageable);
        orders.forEach(this::updateOrderStatusOnFetch);
        return orders.map(OrderDto::from);
    }

    public OrderWithOrderItemsDto getOrderById(Long orderId, Principal principal) {
        if (principal == null) {
            throw new UnauthorizedAccessException("로그인 후 주문을 취소할 수 있습니다.");
        }

        String currentUsername = principal.getName();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다. (ID: " + orderId + ")"));

        if (!order.getCustomer().getUsername().equals(currentUsername)) {
            throw new UnauthorizedAccessException("본인만 자신의 주문을 열람할 수 있습니다.");
        }

        updateOrderStatusOnFetch(order);
        return OrderWithOrderItemsDto.from(order);
    }

    private List<OrderItemDto> validateNewOrder(List<OrderItemDto> orderItemDtos) {
        if (orderItemDtos.isEmpty()) {
            throw new IllegalArgumentException("주문 아이템이 비어 있을 수 없습니다.");
        }
        return orderItemDtos.stream()
                .filter(dto -> itemService.getItemById(dto.getItemId()).isPresent())
                .toList();
    }

    private List<OrderItemDto> validateUpdatedOrder(List<OrderItemDto> orderItemDtos, Order existingOrder) {
        long totalQuantity = 0;
        for (OrderItemDto orderItemDto : orderItemDtos) {
            totalQuantity += orderItemDto.getQuantity();
        }


        for (OrderItem existingItem : existingOrder.getOrderItems()) {
            boolean isValidItem = orderItemDtos.stream()
                    .anyMatch(dto -> dto.getItemId().equals(existingItem.getItem().getId()));
            if (!isValidItem) {
                throw new ItemNotFoundException("아이템이 유효하지 않습니다.");
            }
        }

        return orderItemDtos;
    }


    // 주문 목록 조회
    @Deprecated
    // TODO: use getOrdersByPrinciple instead
    public Page<OrderWithOrderItemsDto> getOrdersByCustomerId(Long customerId, Pageable pageable) {
        Page<Order> orderPage = orderRepository.findAllByCustomerId(customerId, pageable);
        // 주문 목록 조회 시 배송 상태 최신화
        orderPage.forEach(this::updateOrderStatusOnFetch);

        return orderPage.map(OrderWithOrderItemsDto::from);
    }

    private void updateOrderStatusOnFetch(Order order) {
        LocalDateTime today2PM = LocalDateTime.now().withHour(14).withMinute(0).withSecond(0).withNano(0);

        if (order.getDate().isBefore(today2PM)) {
            order.setDeliveryStatus(DeliveryStatus.SHIPPED);
        } else {
            order.setDeliveryStatus(DeliveryStatus.PROCESSING);
        }
        orderRepository.save(order);
    }
}
