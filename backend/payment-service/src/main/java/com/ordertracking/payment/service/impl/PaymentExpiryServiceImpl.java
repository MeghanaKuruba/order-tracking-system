package com.ordertracking.payment.service.impl;

import com.ordertracking.payment.dto.PaymentExpiredEvent;
import com.ordertracking.payment.entity.Payment;
import com.ordertracking.payment.entity.PaymentStatus;
import com.ordertracking.payment.repository.PaymentRepository;
import com.ordertracking.payment.service.OutboxService;
import com.ordertracking.payment.service.PaymentExpiryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentExpiryServiceImpl implements PaymentExpiryService {

    private final PaymentRepository paymentRepository;

    private final OutboxService outboxService;

    public void expirePendingPayments() {

        LocalDateTime expiryTime = LocalDateTime.now().minusMinutes(1);

        List<Payment> expiredPayments =
                paymentRepository.findByStatusAndUpdatedAtBefore(
                        PaymentStatus.PENDING_PAYMENT,
                        expiryTime
                );

        for (Payment payment : expiredPayments) {

            payment.setStatus(PaymentStatus.EXPIRED);
            payment.setFailureReason("Payment expired due to inactivity");

            paymentRepository.save(payment);

            log.info("Payment {} expired", payment.getPaymentId());

            PaymentExpiredEvent event =
                    new PaymentExpiredEvent(
                            payment.getOrderId(),
                            payment.getPaymentId(),
                            payment.getStatus().name(),
                            payment.getAmount()
                    );

            outboxService.saveEvent(
                    "PAYMENT",
                    payment.getPaymentId(),
                    "PAYMENT_EXPIRED",
                    event
            );
        }
    }
}
