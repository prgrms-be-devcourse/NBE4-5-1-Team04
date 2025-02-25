package com.team4.project1.global.init;

import com.team4.project1.domain.customer.entity.Customer;
import com.team4.project1.domain.customer.service.CustomerService;
import com.team4.project1.domain.item.service.ItemService;
import com.team4.project1.domain.order.dto.OrderItemDto;
import com.team4.project1.domain.order.dto.OrderWithOrderItemsDto;
import com.team4.project1.domain.order.repository.OrderRepository;
import com.team4.project1.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 애플리케이션 실행 시 기본 데이터를 초기화하는 클래스입니다.
 * 이 클래스는 고객, 상품, 주문 데이터를 초기화하며,
 * 기본 데이터가 없을 경우에만 실행됩니다.
 */
@Configuration
@RequiredArgsConstructor
public class BaseInitData {

    private final OrderService orderService;
    private final CustomerService customerService;
    private final ItemService itemService;
    private final OrderRepository orderRepository;

    @Autowired
    @Lazy
    private BaseInitData self;

    /**
     * 애플리케이션 실행 시 초기 데이터를 설정하는 {@link ApplicationRunner} Bean을 제공합니다.
     * applicationRunner 메서드는 {@link customerInit()}, {@link itemInit()}, {@link orderInit()} 메서드를 순차적으로 실행하여
     * 고객, 상품, 주문 데이터를 초기화합니다.
     *
     * @return {@link ApplicationRunner} Bean
     */
    @Bean
    public ApplicationRunner applicationRunner() {
        return args -> {
            self.customerInit();
            self.itemInit();
            self.orderInit();
        };
    }

    /**
     * 고객 데이터를 초기화합니다.
     * 기존에 고객 데이터가 없을 경우, 기본 고객 정보를 추가합니다.
     */
    @Transactional
    public void customerInit() {
        if (customerService.count() > 0) {
            return;
        }

        customerService.join("jjang9", "jjang1234", "짱구", "jjang9@example.com");
        customerService.join("cheolsu", "cheolsu1234", "철수", "cheolsu@example.com");
        customerService.join("yuli", "yuli1234", "유리", "yuli@example.com");
        customerService.join("maeng9", "maeng1234", "맹구", "maeng9@example.com");
    }

    /**
     * 상품 데이터를 초기화합니다.
     * 기존에 상품 데이터가 없을 경우, 기본 상품 정보를 추가합니다.
     */
    @Transactional
    public void itemInit() {
        if (itemService.count() > 0) {
            return;
        }
        itemService.addItem("스타벅스", 6800, 10);
        itemService.addItem("맥심커피", 1000, 10);
        itemService.addItem("카누커피", 1500, 10);
        itemService.addItem("컴포즈 커피", 2800, 10);
        itemService.addItem("이디야 커피", 3000, 10);
        itemService.addItem("빽다방", 2000, 10);
        itemService.addItem("커피빈", 4000, 10);
        itemService.addItem("투썸플레이스", 5000, 10);
        itemService.addItem("엔젤리너스", 3500, 10);
        itemService.addItem("더벤티", 4500, 10);
        itemService.addItem("탐앤탐스", 4200, 10);
        itemService.addItem("폴바셋", 5500, 10);
        itemService.addItem("할리스", 4000, 10);
    }

    /**
     * 주문 데이터를 초기화합니다.
     * 기존에 주문 데이터가 없을 경우, 임의로 고객 1번에 대한 주문을 생성합니다.
     * 주문 생성 시, 인증된 사용자로 설정되어 실제 주문을 생성하는 예시가 포함됩니다.
     */
    @Transactional
    public void orderInit() {
        if (orderRepository.count() > 0) {
            return;
        }

        // 임의로 고객 id 1번을 사용 (이미 customerInit에서 데이터가 생성되었음)
        Optional<Customer> customerOpt = customerService.getCustomerById(1L);
        if (customerOpt.isEmpty()) {
            return;
        }
        Customer customer = customerOpt.get();

        // 인증된 사용자로 설정
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                customer.getUsername(),  // 실제 사용자의 이름
                "dummyPassword",         // 더미 비밀번호 (실제 로그인에서는 비밀번호를 체크하지만, 이곳은 더미로 사용)
                List.of(new SimpleGrantedAuthority("ROLE_USER"))  // 사용자 권한
        );
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        // 주문 항목 데이터 생성: 예를 들어, item id 1번 상품을 3개, item id 2번 상품을 2개 주문
        List<OrderItemDto> orderItemDtos = new ArrayList<>();
        orderItemDtos.add(new OrderItemDto(1L, 3));
        orderItemDtos.add(new OrderItemDto(2L, 2));

        // Principal을 이용하여 주문 생성
        Principal principal = new Principal() {
            @Override
            public String getName() {
                return customer.getUsername(); // 인증된 사용자 이름 반환
            }
        };

        // OrderService의 createOrder() 메서드를 호출하여 주문 생성
        OrderWithOrderItemsDto createdOrder = orderService.createOrder(orderItemDtos, principal);

        // TODO: stdout으로 바로 출력하는 대신 로깅(@Slf4j)으로 출력하게끔 변경을 고려
        System.out.println("생성된 주문 ID: " + createdOrder.getId() + ", 총 가격: " + createdOrder.getTotalPrice());
    }
}
