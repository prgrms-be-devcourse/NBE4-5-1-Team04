package com.team4.project1.domain.order.scheduler;

import com.team4.project1.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeliveryScheduler {
    private final OrderService orderService;

    @Scheduled(cron = "0 0 14 * * ?")
    public void scheduleDeliveryUpdate() {
        orderService.updateDeliveryStatus();
    }

}
