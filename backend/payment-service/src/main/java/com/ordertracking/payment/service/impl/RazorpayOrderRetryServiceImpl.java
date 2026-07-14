package com.ordertracking.payment.service.impl;

import com.ordertracking.payment.config.RazorpayOrderRetryProperties;
import com.ordertracking.payment.dto.PaymentEvent;
import com.ordertracking.payment.entity.Payment;
import com.ordertracking.payment.entity.PaymentStatus;
import com.ordertracking.payment.repository.PaymentRepository;
import com.ordertracking.payment.service.OutboxService;
import com.ordertracking.payment.service.RazorpayOrderRetryService;
import com.ordertracking.payment.service.RazorpayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RazorpayOrderRetryServiceImpl
        implements RazorpayOrderRetryService {

    private final PaymentRepository paymentRepository;

    private final RazorpayService razorpayService;

    private final OutboxService outboxService;

    private final RazorpayOrderRetryProperties retryProperties;

    @Override
    public void retryPendingPayments() {

        LocalDateTime retryTime =
                LocalDateTime.now()
                        .minusSeconds(
                                retryProperties.getRetryIntervalSeconds()
                        );

        List<Payment> payments =
                paymentRepository
                        .findByStatusAndRazorpayOrderIdIsNullAndRazorpayRetryCountLessThanAndLastRetryAtBefore(
                                PaymentStatus.PENDING_PAYMENT,
                                retryProperties.getMaxAttempts(),
                                retryTime
                        );

        for (Payment payment : payments) {

            try {

                String razorpayOrderId =
                        razorpayService.createRazorpayOrder(
                                payment.getOrderId(),
                                payment.getAmount()
                        );

                payment.setRazorpayOrderId(razorpayOrderId);

                payment.setLastRetryAt(LocalDateTime.now());

                payment.setFailureReason(null);

                paymentRepository.save(payment);

                log.info(
                        "Successfully created Razorpay Order {}",
                        payment.getPaymentId()
                );

            } catch (Exception ex) {

                payment.setRazorpayRetryCount(
                        payment.getRazorpayRetryCount() + 1
                );

                payment.setLastRetryAt(LocalDateTime.now());

                paymentRepository.save(payment);

                log.warn(
                        "Retry {} failed for payment {}",
                        payment.getRazorpayRetryCount(),
                        payment.getPaymentId()
                );

                if (payment.getRazorpayRetryCount()
                        >= retryProperties.getMaxAttempts()) {

                    markPaymentFailed(payment);

                }

            }

        }

    }

    private void markPaymentFailed(Payment payment) {

        payment.setStatus(PaymentStatus.FAILED);

        payment.setFailureReason(
                "Unable to create Razorpay order after maximum retries"
        );

        paymentRepository.save(payment);

        PaymentEvent event =
                new PaymentEvent(
                        payment.getOrderId(),
                        payment.getPaymentId(),
                        payment.getStatus().name(),
                        payment.getAmount(),
                        null,
                        null,
                        payment.getFailureReason()
                );

        outboxService.saveEvent(
                "PAYMENT",
                payment.getPaymentId(),
                "PAYMENT_EVENT",
                event
        );

        log.info(
                "Payment {} marked FAILED after max retries",
                payment.getPaymentId()
        );

    }

}