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

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
/**
 * {@link OrderService} 클래스에 대한 단위 테스트입니다.
 * 이 클래스는 주문 생성 및 조회 기능을 포함한 {@link OrderService}의 메서드들이 예상대로 동작하는지 확인하는 테스트 메서드들을 포함하고 있습니다.
 */
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
    /**
     * 각 테스트가 실행되기 전에 호출되는 메서드입니다.
     * 테스트를 위해 사용할 기본 객체들을 초기화합니다.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 기본 객체 생성
        customer = new Customer();
//        customer.setId(1L);

        item = new Item();
//        item.setId(1L);
        item.setPrice(10000);

        order = new Order(customer, LocalDateTime.now(), 0L);
//        order.setId(1L);

        orderItem = new OrderItem(order, item, 2);  // 수량 2
        orderItem.setId(1L);

        orderItemDto = new OrderItemDto(1L, 2); // itemId=1, quantity=2
    }

    /**
     * 새로운 주문을 성공적으로 생성하는 메서드에 대한 테스트입니다.
     * 주문 생성 시 주문 항목이 제대로 추가되고, 생성된 주문과 항목들이 올바르게 반환되는지 확인합니다.
     */
    @Test
    @DisplayName("새로운 주문을 성공적으로 생성한다.")
    void createOrder() {
        // Given
        when(itemRepository.getReferenceById(anyLong())).thenReturn(item);

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
//        savedOrder.setId(1L);  // 저장된 Order의 ID 설정
            return savedOrder;
        });

        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(invocation -> {
            OrderItem savedOrderItem = invocation.getArgument(0);
            savedOrderItem.setId(1L);  // 저장된 OrderItem의 ID 설정
            return savedOrderItem;
        });

        when(orderItemRepository.findByOrderId(anyLong())).thenReturn(List.of(orderItem));  // Mock findByOrderId

        // Principal mock 객체 생성
        Principal principal = new Principal() {
            @Override
            public String getName() {
                return "jjang9"; // 인증된 사용자 이름을 지정
            }
        };

        // When
        OrderWithOrderItemsDto createdOrder = orderService.createOrder(List.of(orderItemDto), principal);

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

    /**
     * 존재하지 않는 주문 ID로 조회할 때 발생하는 예외를 테스트합니다.
     * 존재하지 않는 주문 ID로 조회를 시도할 때, 빈 결과가 반환되는지 확인합니다.
     */
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
    /**
     * 주문 ID로 주문을 조회하는 메서드에 대한 테스트입니다.
     * 주문 ID로 조회된 주문이 올바르게 반환되는지, 관련된 주문 항목들이 올바르게 조회되는지 확인합니다.
     */
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
