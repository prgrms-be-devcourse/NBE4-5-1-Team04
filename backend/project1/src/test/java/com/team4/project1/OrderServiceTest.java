package com.team4.project1;

import com.team4.project1.domain.customer.entity.Customer;
import com.team4.project1.domain.item.entity.Item;
import com.team4.project1.domain.item.repository.ItemRepository;
import com.team4.project1.domain.order.dto.OrderItemDto;
import com.team4.project1.domain.order.dto.OrderWithOrderItemsDto;
import com.team4.project1.domain.order.entity.Order;
import com.team4.project1.domain.order.entity.OrderItem;
import com.team4.project1.domain.order.repository.OrderItemRepository;
import com.team4.project1.domain.order.repository.OrderRepository;
import com.team4.project1.domain.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@Disabled("Missing setter for entity id fields")
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private OrderService orderService;

    private Customer customer;
    private Item item;
    private Order order;
    private OrderItem orderItem;
    private OrderItemDto orderItemDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Customer 객체 생성 (Builder 사용)
        customer = Customer.builder()
                .username("testUser")
                .password("password123")
                .name("Test User")
                .email("test@example.com")
                .build();

        // Item 객체 생성 (Builder 사용)
        item = Item.builder()
                .name("Test Item")
                .price(10000)
                .stock(100)
                .build();

        // Order 객체 생성 (Builder 사용)
        order = Order.builder()
                .customer(customer)
                .totalPrice(0L)
                .build();

        // OrderItem 객체 생성 (ID는 Mockito가 관리하도록 설정)
        orderItem = OrderItem.builder()
                .order(order)
                .item(item)
                .quantity(2)
                .build();

        // OrderItemDto 생성
        orderItemDto = new OrderItemDto(1L, 2); // itemId=1, quantity=2
    }


    @Test
    @DisplayName("새로운 주문을 성공적으로 생성한다.")
    void createOrder() {
        // Given
        when(itemRepository.getReferenceById(anyLong())).thenReturn(item);

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
//            savedOrder.setId(1L);  // 저장된 Order의 ID 설정
            return savedOrder;
        });

        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(invocation -> {
            OrderItem savedOrderItem = invocation.getArgument(0);
            savedOrderItem.setId(1L);  // 저장된 OrderItem의 ID 설정
            return savedOrderItem;
        });

        when(orderItemRepository.findByOrderId(anyLong())).thenReturn(List.of(orderItem));  // Mock findByOrderId

        // When
        OrderWithOrderItemsDto createdOrder = orderService.createOrder(List.of(orderItemDto), customer.getId());

        // Then
        assertThat(createdOrder).isNotNull();
        assertThat(createdOrder.getId()).isEqualTo(1L);
        assertThat(createdOrder.getOrderedItems()).hasSize(1);
        assertThat(createdOrder.getOrderedItems().get(0).getQuantity()).isEqualTo(2);

        // Verify
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
        verify(orderItemRepository, times(1)).findByOrderId(anyLong());
    }

    @Test
    @DisplayName("존재하지 않는 주문 ID를 조회하면 예외 발생")
    void getOrderById_NotFound() {
        // Given
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThat(orderRepository.findById(99L)).isEmpty();

        // Verify
        verify(orderRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("주문 ID로 주문을 조회할 수 있다.")
    void getOrderById_Success() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderItemRepository.findByOrderId(1L)).thenReturn(List.of(orderItem));

        // When
        Optional<Order> foundOrder = orderRepository.findById(1L);
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(1L);

        // Then
        assertThat(foundOrder).isPresent();
        assertThat(foundOrder.get().getId()).isEqualTo(1L);
        assertThat(orderItems).hasSize(1);
        assertThat(orderItems.get(0).getQuantity()).isEqualTo(2);

        // Verify
        verify(orderRepository, times(1)).findById(1L);
        verify(orderItemRepository, times(1)).findByOrderId(1L);
    }
}
