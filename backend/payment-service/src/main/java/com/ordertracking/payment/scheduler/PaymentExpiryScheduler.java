package com.ordertracking.payment.scheduler;

import com.ordertracking.payment.service.PaymentExpiryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentExpiryScheduler {

    private final PaymentExpiryService paymentExpiryService;

    @Transactional
    @Scheduled(fixedRate = 10000)
    public void expirePendingPayments() {
        paymentExpiryService.expirePendingPayments();
    }
}