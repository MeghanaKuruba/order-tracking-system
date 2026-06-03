package com.ordertracking.delivery.scheduler;

import com.ordertracking.delivery.service.DeliveryPartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeliveryMonitoringSchelduler {

    private final DeliveryPartnerService deliveryPartnerService;

    @Scheduled(fixedDelay = 5000) // Run every 5 seconds
    public void monitorOfflinePartners() {
        deliveryPartnerService.monitorOfflinePartners();
    }
}
