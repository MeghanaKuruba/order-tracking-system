package com.ordertracking.payment.scheduler;

import com.ordertracking.payment.service.RazorpayOrderRetryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RazorpayOrderRetryScheduler{

    private final RazorpayOrderRetryService razorpayOrderRetryService;

    @Scheduled(
            fixedDelayString =
                    "${payment.razorpay-order.retry-interval-seconds}"
    )
    public void retryPendingPayments() {

        razorpayOrderRetryService.retryPendingPayments();

    }

}
