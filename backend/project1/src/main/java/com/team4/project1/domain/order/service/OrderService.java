package com.team4.project1.domain.order.service;

import com.team4.project1.domain.customer.entity.Customer;
import com.team4.project1.domain.customer.service.CustomerService;
import com.team4.project1.domain.item.entity.Item;
import com.team4.project1.domain.item.service.ItemService;
import com.team4.project1.domain.order.dto.OrderDto;
import com.team4.project1.domain.order.dto.OrderItemDto;
import com.team4.project1.domain.order.dto.OrderWithOrderItemsDto;
import com.team4.project1.domain.order.entity.Order;
import com.team4.project1.domain.order.entity.OrderItem;
import com.team4.project1.domain.order.entity.OrderStatus;
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

/**
 * 주문 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * 주문 생성, 수정, 취소 및 조회 기능을 제공합니다.
 * 주문 처리 과정에서 고객, 아이템, 주문 상태 등을 검증하고 업데이트합니다.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ItemService itemService;
    private final CustomerService customerService;

    /**
     * 새로운 주문을 생성합니다.
     *
     * @param orderItemDtos 주문할 아이템 목록 (ID 및 수량 포함)
     * @param principal     현재 로그인한 사용자 정보
     * @return 생성된 주문 정보을 반환합니다.
     * @throws UnauthorizedAccessException 사용자가 로그인하지 않았을 경우 예외발생
     * @throws CustomerNotFoundException  현재 사용자 정보로 고객을 찾을 수 없는 경우 예외발생
     * @throws ItemNotFoundException      주문하려는 아이템이 존재하지 않는 경우 예외발생
     */
    //주문 생성 메소드
    public OrderWithOrderItemsDto createOrder(List<OrderItemDto> orderItemDtos, Principal principal) {
        // 비로그인 체크
        if (principal == null) {
            throw new UnauthorizedAccessException("로그인 후 주문을 생성할 수 있습니다.");
        }

        String currentUsername = principal.getName();  // Principal에서 사용자 이름을 가져옵니다.
        Customer customer = customerService.findByUsername(currentUsername);
        if (customer == null) {
            throw new CustomerNotFoundException("사용자를 찾을 수 없습니다.");
        }

        Order newOrder = new Order(customer, LocalDateTime.now(), 0L);

        orderItemDtos = validateNewOrder(orderItemDtos);  // 주문 아이템 유효성 검사
        long totalPrice = 0L;

        for (OrderItemDto orderItemDto : orderItemDtos) {
            Item item = itemService.getItemById(orderItemDto.getItemId()).toEntity();

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

    /**
     * 기존 주문을 수정합니다.
     * @param orderItemDtos 수정할 아이템 목록
     * @param orderId       수정할 주문 ID
     * @param principal     현재 로그인한 사용자 정보
     * @return 수정된 주문 정보를 반환합니다.
     * @throws UnauthorizedAccessException 본인이 아닌 사용자가 접근할 경우 예외 발생
     * @throws IllegalStateException       이미 발송된 주문을 수정하려 할 경우 예외 발생
     */
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

        updateOrderStatus(existingOrder);
        if (existingOrder.getOrderStatus() == OrderStatus.SHIPPED) {
            throw new IllegalStateException("이미 발송된 주문은 수정할 수 없습니다.");
        }

        orderItemDtos = validateUpdatedOrder(orderItemDtos, existingOrder);  // 수정된 유효성 검사 호출
        existingOrder.getOrderItems().clear();

        long totalPrice = 0L;
        for (OrderItemDto orderItemDto : orderItemDtos) {
            Item item = itemService.getItemById(orderItemDto.getItemId()).toEntity();

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

    /**
     * 주문을 취소합니다.
     * @param orderId   취소할 주문 ID
     * @param principal 현재 로그인한 사용자 정보
     * @return 취소된 주문 ID을 반환합니다.
     * @throws UnauthorizedAccessException 본인이 아닌 사용자가 접근할 경우 예외발생
     * @throws IllegalStateException       이미 발송된 주문을 취소하려 할 경우 예외발생
     */
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

        updateOrderStatus(existingOrder);
        if (existingOrder.getOrderStatus() == OrderStatus.SHIPPED) {
            throw new IllegalStateException("이미 발송된 주문은 취소할 수 없습니다.");
        }

        orderRepository.deleteById(orderId);
        return orderId;
    }

    /**
     * 주문을 접수합니다.
     * @param orderId   취소할 주문 ID
     * @param principal 현재 로그인한 사용자 정보
     * @return 접수된 주문 ID을 반환합니다.
     * @throws UnauthorizedAccessException 본인이 아닌 사용자가 접근할 경우 예외발생
     * @throws IllegalStateException       이미 접수됐거나 발송된 주문을 접수하려 할 경우 예외발생
     */
    public Long confirmOrder(Long orderId, Principal principal) {
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다. (ID: " + orderId + ")"));

        if (principal == null) {
            throw new UnauthorizedAccessException("로그인 후 주문을 접수할 수 있습니다.");
        }

        String currentUsername = principal.getName();
        if (!existingOrder.getCustomer().getUsername().equals(currentUsername)) {
            throw new UnauthorizedAccessException("본인만 자신의 주문을 접수할 수 있습니다.");
        }

        if (existingOrder.getOrderStatus() == OrderStatus.SHIPPED ||
                existingOrder.getOrderStatus() == OrderStatus.PROCESSING) {
            throw new IllegalStateException("이미 접수됐거나 발송된 주문은 또 접수할 수 없습니다.");
        }

        existingOrder.setOrderStatus(OrderStatus.PROCESSING);
        existingOrder.setDate(LocalDateTime.now());
        orderRepository.save(existingOrder);
        return orderId;
    }

    /**
     * 현재 로그인한 사용자의 주문 목록을 조회합니다.
     * @param principal 현재 로그인한 사용자 정보
     * @return 사용자의 주문 목록을 반환합니다.
     */
    public Page<OrderDto> getOrdersByPrincipal(Principal principal, Pageable pageable) {
        if (principal == null) {
            throw new UnauthorizedAccessException("로그인 후 주문을 조회할 수 있습니다.");
        }

        String currentUsername = principal.getName();
        Customer customer = customerService.findByUsername(currentUsername);
        if (customer == null) {
            throw new CustomerNotFoundException("사용자를 찾을 수 없습니다.");
        }


        Page<Order> orders = orderRepository.findAllByCustomerId(customer.getId(), pageable);
        orders.forEach(this::updateOrderStatus);
        return orders.map(OrderDto::from);
    }

    /**
     * 주문 ID로 주문을 조회합니다.
     * @param orderId   조회할 주문 ID
     * @param principal 현재 로그인한 사용자 정보
     * @return 주문 상세 정보를 반환합니다.
     * @throws UnauthorizedAccessException 로그인한 사용자가 아닌 다른 사용자가 주문을 조회하려 할 때 예외 발생
     */
    public OrderWithOrderItemsDto getOrderById(Long orderId, Principal principal) {
        if (principal == null) {
            throw new UnauthorizedAccessException("로그인 후 주문을 조회할 수 있습니다.");
        }

        String currentUsername = principal.getName();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다. (ID: " + orderId + ")"));

        if (!order.getCustomer().getUsername().equals(currentUsername)) {
            throw new UnauthorizedAccessException("본인만 자신의 주문을 열람할 수 있습니다.");
        }

        updateOrderStatus(order);
        return OrderWithOrderItemsDto.from(order);
    }

    /**
     * 새로운 주문 아이템의 유효성을 검사합니다.
     아이템 수량이 1 이상이어야 하고, 주문 아이템이 비어 있지 않도록 체크합니다.
     * @param orderItemDtos 주문할 아이템의 목록 (각 아이템의 수량 포함)
     * @return 유효성이 확인된 아이템 목록을 반환합니다.
     * @throws IllegalArgumentException 주문 아이템이 비어 있거나, 수량이 1 미만인 경우 예외 발생
     */
    // 새로운 주문 아이템 유효성 검사
    private List<OrderItemDto> validateNewOrder(List<OrderItemDto> orderItemDtos) {
        if (orderItemDtos.isEmpty()) {
            throw new IllegalArgumentException("주문 아이템이 비어 있을 수 없습니다.");
        }
        return orderItemDtos.stream()
                .map(dto -> {
                    itemService.getItemById(dto.getItemId()); // 존재 여부 확인, 존재하지 않으면 예외 발생
                    return dto;
                })
                .toList();
    }

    /**
     * 기존 주문 아이템의 유효성을 검사합니다.
     * 기존 주문에 포함된 아이템들과 수정된 주문 아이템을 비교하여 유효성을 검증합니다.
     * @param orderItemDtos 수정된 아이템의 목록
     * @param existingOrder 기존 주문 정보
     * @return 유효성이 확인된 수정된 아이템 목록을 반환합니다.
     * @throws ItemNotFoundException 주문 아이템 중 유효하지 않은 아이템이 있을 경우 예외 발생
     */
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

    /**
     * 주문 상태를 조회하여, 발송 여부를 결정합니다.
     * 주문 날짜가 오늘 오후 2시 이전이라면 'SHIPPED' 상태로 설정됩니다. 그 외에는 'PROCESSING' 상태로 설정됩니다.
     * @param order 상태를 갱신할 주문 정보
     */
    private void updateOrderStatus(Order order) {
        // 아직 주문이 장바구니 상태라면 배송 상태를 업데이트 하지 않는다.
        if (order.getOrderStatus() == OrderStatus.TEMPORARY) {
            return;
        }
        LocalDateTime today2PM = LocalDateTime.now().withHour(14).withMinute(0).withSecond(0).withNano(0);

        if (order.getDate().isBefore(today2PM)) {
            order.setOrderStatus(OrderStatus.SHIPPED);
        } else {
            order.setOrderStatus(OrderStatus.PROCESSING);
        }
        orderRepository.save(order);
    }

}
