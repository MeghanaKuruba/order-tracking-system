package com.ordertracking.payment.service.impl;

import com.ordertracking.payment.config.razorpay.RazorpayProperties;
import com.ordertracking.payment.dto.PaymentCheckoutResponse;
import com.ordertracking.payment.dto.PaymentFailureEvent;
import com.ordertracking.payment.dto.PaymentResponse;
import com.ordertracking.payment.entity.Payment;
import com.ordertracking.payment.entity.PaymentStatus;
import com.ordertracking.payment.exception.*;
import com.ordertracking.payment.kafka.producer.PaymentEventProducer;
import com.ordertracking.payment.mapper.PaymentMapper;
import com.ordertracking.payment.repository.PaymentRepository;
import com.ordertracking.payment.service.PaymentService;
import com.ordertracking.payment.service.RazorpayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;


@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    private final PaymentMapper paymentMapper;

    private final RazorpayService razorpayService;

    private final RazorpayProperties razorpayProperties;

    private final PaymentEventProducer paymentEventProducer;

    @Value("${payment.max-attempts}")
    private int maxPaymentAttempts;

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

    @Transactional(noRollbackFor = PaymentRetryLimitExceededException.class)
    @Override
    public PaymentCheckoutResponse retryPayment(Long orderId) {

        log.info("Retrying payment for orderId={}", orderId);

        Payment lastPayment = paymentRepository
                .findTopByOrderIdOrderByCreatedAtDesc(orderId)
                .orElseThrow(() -> new PaymentNotFoundException("No payment found for order id : " + orderId));

        if (lastPayment.getStatus() == PaymentStatus.SUCCESS) {
            throw new PaymentAlreadyProcessedException("Payment already completed for order id : " + orderId);
        }

        if (lastPayment.getAttemptNumber() > maxPaymentAttempts){
            lastPayment.setStatus(PaymentStatus.EXPIRED);
            paymentRepository.save(lastPayment);
            throw new PaymentRetryLimitExceededException("Maximum payment retry attempts exceeded for order is: " + orderId);
        }

        String razorpayOrderId = razorpayService.createRazorpayOrder(
                lastPayment.getPaymentId(), lastPayment.getAmount()
        );

        lastPayment.setAttemptNumber(lastPayment.getAttemptNumber() + 1);
        lastPayment.setRazorpayOrderId(razorpayOrderId);
        lastPayment.setStatus(PaymentStatus.PENDING_PAYMENT);
        lastPayment.setTransactionId(null);
        lastPayment.setPaymentMethod(null);
        lastPayment.setFailureReason(null);

        Payment savedPayment = paymentRepository.save(lastPayment);

        log.info("Retry created: paymentId={}, razorpayOrderId={}",
                savedPayment.getPaymentId(), razorpayOrderId);

        return PaymentCheckoutResponse.builder()
                .razorpayOrderId(savedPayment.getRazorpayOrderId())
                .amount(savedPayment.getAmount())
                .currency("INR")
                .keyId(razorpayProperties.getKeyId())
                .build();
    }
}
