package com.ordertracking.payment.service.impl;

import com.ordertracking.payment.dto.PaymentFailureEvent;
import com.ordertracking.payment.dto.PaymentResponse;
import com.ordertracking.payment.entity.Payment;
import com.ordertracking.payment.entity.PaymentStatus;
import com.ordertracking.payment.exception.InvalidPaymentStateException;
import com.ordertracking.payment.exception.PaymentNotFoundException;
import com.ordertracking.payment.kafka.producer.PaymentEventProducer;
import com.ordertracking.payment.mapper.PaymentMapper;
import com.ordertracking.payment.repository.PaymentRepository;
import com.ordertracking.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    private final PaymentMapper paymentMapper;

    private final PaymentEventProducer paymentEventProducer;

    @Override
    public PaymentResponse getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found for order ID: " + orderId));
        return paymentMapper.mapToPaymentResponse(payment);
    }

    @Override
    @Transactional
    public Payment markPaymentAsFailed(Long paymentId, String reason) {

        log.warn("Attempting to mark payment {} as FAILED", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new PaymentNotFoundException("Payment not found with id: " + paymentId)
                );

        // Prevent invalid state transition
        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            throw new InvalidPaymentStateException("Cannot mark a successful payment as failed");
        }

        // Idempotency
        if (payment.getStatus() == PaymentStatus.FAILED) {
            log.info("Payment {} already marked as FAILED (idempotent call)", paymentId);
            return payment;
        }

        // Update failure details
        payment.setStatus(PaymentStatus.FAILED);
        payment.setFailureReason(
                (reason == null || reason.isBlank()) ? "Unknown failure" : reason
        );

        Payment savedPayment = paymentRepository.save(payment);

        // Publish failure event (safe)
        try {
            PaymentFailureEvent event = new PaymentFailureEvent(
                    savedPayment.getOrderId(),
                    savedPayment.getPaymentId(),
                    savedPayment.getStatus().name(),
                    savedPayment.getFailureReason(),
                    savedPayment.getAmount()
            );

            paymentEventProducer.sendPaymentFailureEvent(event);

        } catch (Exception ex) {
            log.error("Failed to publish payment-failure event for paymentId={}, error={}",
                    savedPayment.getPaymentId(), ex.getMessage(), ex);
        }

        log.info("Payment {} marked as FAILED successfully", paymentId);

        return savedPayment;
    }
}
