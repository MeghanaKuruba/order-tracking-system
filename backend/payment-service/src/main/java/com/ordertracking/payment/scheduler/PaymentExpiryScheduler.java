package com.ordertracking.payment.scheduler;

import com.ordertracking.payment.entity.Payment;
import com.ordertracking.payment.entity.PaymentStatus;
import com.ordertracking.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentExpiryScheduler {

    private final PaymentRepository paymentRepository;

    @Scheduled(fixedRate = 10000)
    private void expirePendingPayment() {
        LocalDateTime expiryTime = LocalDateTime.now().minusMinutes(1);

        List<Payment> expiredPayment = paymentRepository.findByStatusAndUpdatedAtBefore(
                PaymentStatus.PENDING_PAYMENT, expiryTime
        );

        for (Payment payment : expiredPayment){
            payment.setStatus(PaymentStatus.EXPIRED);

            payment.setFailureReason("Payment expired due to inactivity");

            log.info("Payment {} expired", payment.getPaymentId());
        }
        paymentRepository.saveAll(expiredPayment);
    }
}
