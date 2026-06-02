package com.ordertracking.delivery.scheduler;

import com.ordertracking.delivery.service.DeliveryPartnerService;
import com.ordertracking.delivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeliveryAssignmentScheduler {

    private final DeliveryPartnerService deliveryPartnerService;

    @Scheduled(fixedDelay = 30000) // Run every 30 seconds
    public void retryPartnerAssignment() {
        log.info("Running scheduled task to assign pending deliveries...");
        deliveryPartnerService.retryPartnerAssignment();
    }
}
