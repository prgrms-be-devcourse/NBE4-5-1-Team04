package com.team4.project1.domain.order.service;

import com.team4.project1.domain.customer.entity.Customer;
import com.team4.project1.domain.customer.service.CustomerService;
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
import com.team4.project1.global.exception.InvalidOrderQuantityException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

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

    // 주문 생성 메소드
    public OrderWithOrderItemsDto createOrder(List<OrderItemDto> orderItemDtos, Long customerId) {
        // 비로그인 체크
        if (SecurityContextHolder.getContext().getAuthentication() == null ||
                SecurityContextHolder.getContext().getAuthentication().getPrincipal() == null) {
            throw new UnauthorizedAccessException("로그인 후 주문을 생성할 수 있습니다.");
        }

        validateCustomerOwnership(customerId);  // 사용자 인증 확인

        Customer customer = customerService.getCustomerById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        Order newOrder = new Order(customer, LocalDateTime.now(), 0L);

        orderItemDtos = validateNewOrder(orderItemDtos);  // 주문 아이템 유효성 검사
        long totalPrice = 0L;

        for (OrderItemDto orderItemDto : orderItemDtos) {
            Item item = itemService.getItemById(orderItemDto.getItemId())
                    .map(Item::fromDto)
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

    // 주문 수정 메소드
    public OrderWithOrderItemsDto updateOrder(List<OrderItemDto> orderItemDtos, Long orderId) {
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다. (ID: " + orderId + ")"));

        String currentUsername = getCurrentUsername();
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
        return OrderWithOrderItemsDto.from(existingOrder);
    }

    // 주문 취소 메소드
    public Long cancelOrder(Long orderId) {
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다. (ID: " + orderId + ")"));

        String currentUsername = getCurrentUsername();
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

    // 주문 목록 조회
    public List<OrderDto> getOrdersByCustomerId(Long customerId) {
        // 비로그인 체크
        if (SecurityContextHolder.getContext().getAuthentication() == null ||
                SecurityContextHolder.getContext().getAuthentication().getPrincipal() == null) {
            throw new UnauthorizedAccessException("로그인 후 주문 목록을 조회할 수 있습니다.");
        }

        validateCustomerOwnership(customerId);  // 사용자 인증 확인

        List<Order> orders = orderRepository.findAllByCustomerId(customerId);
        orders.forEach(this::updateOrderStatusOnFetch);
        return orders.stream()
                .map(OrderDto::from)
                .toList();
    }

    // 주문 단건 조회
    public OrderWithOrderItemsDto getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다. (ID: " + orderId + ")"));

        String currentUsername = getCurrentUsername();
        if (!order.getCustomer().getUsername().equals(currentUsername)) {
            throw new UnauthorizedAccessException("본인만 자신의 주문을 열람할 수 있습니다.");
        }

        updateOrderStatusOnFetch(order);
        return OrderWithOrderItemsDto.from(order);
    }

    // 새로운 주문 아이템 유효성 검사
    private List<OrderItemDto> validateNewOrder(List<OrderItemDto> orderItemDtos) {
        if (orderItemDtos.isEmpty()) {
            throw new IllegalArgumentException("주문 아이템이 비어 있을 수 없습니다.");
        }
        for (OrderItemDto item : orderItemDtos) {
            if (item.getQuantity() <= 0) {
                throw new IllegalArgumentException("아이템 수량은 1 이상이어야 합니다.");
            }
        }
        return orderItemDtos;
    }

    // 기존 주문 아이템 수정 시 유효성 검사
    private List<OrderItemDto> validateUpdatedOrder(List<OrderItemDto> orderItemDtos, Order existingOrder) {
        long totalQuantity = 0;
        for (OrderItemDto orderItemDto : orderItemDtos) {
            if (orderItemDto.getQuantity() <= 0) {
                throw new InvalidOrderQuantityException("수량은 1 이상이어야 합니다.");  // 커스텀 예외 사용
            }
            totalQuantity += orderItemDto.getQuantity();
        }

        if (totalQuantity > 100) {
            throw new InvalidOrderQuantityException("주문 수량은 100개를 초과할 수 없습니다.");
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

    // 로그인된 사용자의 username을 반환하는 메소드
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new UnauthorizedAccessException("로그인이 필요합니다.");
        }
        if (authentication.getPrincipal() instanceof String) {
            return (String) authentication.getPrincipal();
        } else if (authentication.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) authentication.getPrincipal()).getUsername();
        }
        return null;
    }

    private void validateCustomerOwnership(Long customerId) {
        // 인증 정보 확인
        String currentUsername = getCurrentUsername();
        Customer customer = customerService.findByUsername(currentUsername)
                .orElseThrow(() -> new UnauthorizedAccessException("로그인된 사용자 정보가 없습니다."));

        if (!customer.getId().equals(customerId)) {
            throw new UnauthorizedAccessException("본인만 해당 주문을 처리할 수 있습니다.");
        }
    }

    // 주문 상태 업데이트
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
