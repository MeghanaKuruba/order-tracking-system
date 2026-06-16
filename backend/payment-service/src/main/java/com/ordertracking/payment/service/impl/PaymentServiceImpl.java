package com.ordertracking.payment.service.impl;

import com.ordertracking.payment.config.razorpay.RazorpayProperties;
import com.ordertracking.payment.dto.PaymentCheckoutResponse;
import com.ordertracking.payment.dto.PaymentFailureEvent;
import com.ordertracking.payment.dto.PaymentResponse;
import com.ordertracking.payment.entity.Payment;
import com.ordertracking.payment.entity.PaymentStatus;
import com.ordertracking.payment.exception.FailedRazorpayOrderCreation;
import com.ordertracking.payment.exception.InvalidPaymentStateException;
import com.ordertracking.payment.exception.PaymentNotFoundException;
import com.ordertracking.payment.kafka.producer.PaymentEventProducer;
import com.ordertracking.payment.mapper.PaymentMapper;
import com.ordertracking.payment.repository.PaymentRepository;
import com.ordertracking.payment.service.PaymentService;
import com.ordertracking.payment.service.RazorpayService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    private final PaymentMapper paymentMapper;

    private final RazorpayService razorpayService;

    private final RazorpayProperties razorpayProperties;

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

    @Transactional
    public PaymentCheckoutResponse retryPayment(Long orderId) {

        log.info("Retrying payment for orderId={}", orderId);

        Payment lastPayment = paymentRepository
                .findTopByOrderIdOrderByCreatedAtDesc(orderId)
                .orElseThrow(() -> new PaymentNotFoundException("No payment found for order"));

        if (lastPayment.getStatus() == PaymentStatus.SUCCESS) {
            throw new InvalidPaymentStateException("Cannot retry a successful payment");
        }

        if (lastPayment.getStatus() == PaymentStatus.PENDING_PAYMENT) {
            throw new InvalidPaymentStateException("Payment already in progress");
        }

        if (lastPayment.getStatus() != PaymentStatus.FAILED) {
            throw new InvalidPaymentStateException("Payment not eligible for retry");
        }

        // Create new payment record
        Payment newPayment = new Payment();
        newPayment.setOrderId(orderId);
        newPayment.setCustomerId(lastPayment.getCustomerId());
        newPayment.setAmount(lastPayment.getAmount());
        newPayment.setStatus(PaymentStatus.PENDING_PAYMENT);
        newPayment.setAttemptNumber(
                lastPayment.getAttemptNumber() == null ? 1 : lastPayment.getAttemptNumber() + 1
        );

        Payment savedPayment = paymentRepository.save(newPayment);

        // Create Razorpay order with correct paymentId
        String razorpayOrderId = razorpayService.createRazorpayOrder(
                savedPayment.getPaymentId(),
                savedPayment.getAmount()
        );

        savedPayment.setRazorpayOrderId(razorpayOrderId);
        paymentRepository.save(savedPayment);

        log.info("Retry created: paymentId={}, razorpayOrderId={}",
                savedPayment.getPaymentId(), razorpayOrderId);

        return new PaymentCheckoutResponse(
                razorpayOrderId,
                razorpayProperties.getKeyId(),
                savedPayment.getAmount(),
                "INR"
        );
    }
}
