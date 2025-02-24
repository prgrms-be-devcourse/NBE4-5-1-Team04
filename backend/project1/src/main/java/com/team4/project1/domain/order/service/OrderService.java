package com.team4.project1.domain.order.service;

import com.team4.project1.domain.customer.entity.Customer;
import com.team4.project1.domain.customer.service.CustomerService;
import com.team4.project1.domain.order.dto.OrderDto;
import com.team4.project1.domain.item.dto.ItemDto;
import com.team4.project1.domain.item.entity.Item;
import com.team4.project1.domain.item.service.ItemService;
import com.team4.project1.domain.order.dto.OrderItemDto;
import com.team4.project1.domain.order.dto.OrderWithOrderItemsDto;
import com.team4.project1.domain.order.entity.DeliveryStatus;
import com.team4.project1.domain.order.entity.Order;
import com.team4.project1.domain.order.entity.OrderItem;
import com.team4.project1.domain.order.repository.OrderItemRepository;
import com.team4.project1.domain.order.repository.OrderRepository;
import com.team4.project1.global.exception.CustomerNotFoundException;
import com.team4.project1.global.exception.ItemNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // 주문 생성 메소드
    public OrderWithOrderItemsDto createOrder(List<OrderItemDto> orderItemDtos, Long customerId) {
        Customer customer = customerService.getCustomerById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        Order newOrder = new Order(customer, java.time.LocalDateTime.now(), 0L);

        orderItemDtos = validateNewOrder(orderItemDtos);
        long totalPrice = 0L;

        for (OrderItemDto orderItemDto : orderItemDtos) {
            // ItemDto를 Item으로 변환
            Item item = itemService.getItemById(orderItemDto.getItemId())
                    .map(Item::fromDto)  // ItemDto를 Item으로 변환
                    .orElseThrow(() -> new ItemNotFoundException(orderItemDto.getItemId()));

            // ✅ 재고 수량 체크 및 감소
            itemService.reduceStock(orderItemDto.getItemId(), orderItemDto.getQuantity());  // 재고 감소 처리

            // 주문 아이템 추가
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

        updateOrderStatusOnFetch(existingOrder);
        //SHIPPED 상태이면 수정 불가
        if(existingOrder.getDeliveryStatus() == DeliveryStatus.SHIPPED) {
            throw new IllegalStateException("이미 발송된 주문은 수정할 수 없습니다.");
        }

        orderItemDtos = validateUpdatedOrder(orderItemDtos, existingOrder);
        existingOrder.getOrderItems().clear();

        long totalPrice = 0L;

        for (OrderItemDto orderItemDto : orderItemDtos) {
            // ItemDto를 Item으로 변환
            Item item = itemService.getItemById(orderItemDto.getItemId())
                    .map(Item::fromDto)  // ItemDto를 Item으로 변환
                    .orElseThrow(() -> new ItemNotFoundException(orderItemDto.getItemId()));

            // ✅ 재고 수량 체크 및 감소
            itemService.reduceStock(orderItemDto.getItemId(), orderItemDto.getQuantity());  // 재고 감소 처리

            OrderItem newOrderItem = new OrderItem(existingOrder, item, orderItemDto.getQuantity());
            existingOrder.getOrderItems().add(newOrderItem);
            totalPrice += (long) newOrderItem.getItem().getPrice() * newOrderItem.getQuantity();
            orderItemRepository.save(newOrderItem);
        }

        existingOrder.setDate(java.time.LocalDateTime.now());
        existingOrder.setTotalPrice(totalPrice);
        orderRepository.save(existingOrder);
        return OrderWithOrderItemsDto.from(existingOrder);
    }

    // 주문 취소 메소드
    public Long cancelOrder(Long orderId) {
        Order existingOrder = orderRepository.findById(orderId)
                        .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다. (ID: " + orderId + ")"));

        updateOrderStatusOnFetch(existingOrder);
        if(existingOrder.getDeliveryStatus() == DeliveryStatus.SHIPPED) {
            throw new IllegalStateException(("이미 발송된 주문은 취소할 수 없습니다."));
        }

        orderRepository.deleteById(orderId);
        return orderId;
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

    // 주문 목록 조회
    public Page<OrderWithOrderItemsDto> getOrdersByCustomerId(Long customerId, Pageable pageable) {
        Page<Order> orderPage = orderRepository.findAllByCustomerId(customerId, pageable);

        // 주문 목록 조회 시 배송 상태 최신화
        orderPage.forEach(this::updateOrderStatusOnFetch);

        return orderPage.map(OrderWithOrderItemsDto::from);
    }

    // 주문 단건 조회
    public OrderWithOrderItemsDto getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다. (ID: " + orderId + ")"));
        updateOrderStatusOnFetch(order);
        return OrderWithOrderItemsDto.from(order);
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
