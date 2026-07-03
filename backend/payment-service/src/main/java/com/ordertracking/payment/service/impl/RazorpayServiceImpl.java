package com.ordertracking.payment.service.impl;

import com.ordertracking.payment.config.razorpay.RazorpayProperties;
import com.ordertracking.payment.dto.PaymentCheckoutResponse;
import com.ordertracking.payment.dto.PaymentSuccessEvent;
import com.ordertracking.payment.dto.PaymentVerificationRequest;
import com.ordertracking.payment.entity.Payment;
import com.ordertracking.payment.entity.PaymentMethod;
import com.ordertracking.payment.entity.PaymentStatus;
import com.ordertracking.payment.exception.*;
import com.ordertracking.payment.repository.PaymentRepository;
import com.ordertracking.payment.service.OutboxService;
import com.ordertracking.payment.service.RazorpayService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class RazorpayServiceImpl implements RazorpayService {

    private static final Logger log = LoggerFactory.getLogger(RazorpayServiceImpl.class);
    private final RazorpayClient razorpayClient;

    private final PaymentRepository paymentRepository;

    private final RazorpayProperties razorpayProperties;

    private final OutboxService outboxService;

    @Override
    public String createRazorpayOrder(Long paymentId, BigDecimal amount) {
        try {
            JSONObject options = new JSONObject();
            options.put("amount", amount.multiply(BigDecimal.valueOf(100)));
            options.put("currency", "INR");
            options.put("receipt", "payment_"+paymentId);

            Order razorpayOrder = razorpayClient.orders.create(options);

            return razorpayOrder.get("id");

        } catch (RazorpayException e) {
            throw new FailedRazorpayOrderCreation("Failed to create Razorpay order");
        }
    }

    @Override
    public PaymentCheckoutResponse getCheckoutDetails(Long paymentId){
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found for ID: " + paymentId));

        return  new PaymentCheckoutResponse(
                payment.getRazorpayOrderId(),
                razorpayProperties.getKeyId(),
                payment.getAmount(),
                "INR"
        );
    }

    @Transactional
    @Override
    public String verifyPayment(PaymentVerificationRequest request) {
        try{
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", request.getRazorpayOrderId());
            options.put("razorpay_payment_id", request.getRazorpayPaymentId());
            options.put("razorpay_signature", request.getRazorpaySignature());

            boolean isValid = Utils.verifyPaymentSignature(options, razorpayProperties.getKeySecret());

            if(!isValid){
                throw new PaymentVerificationException("Invalid payment signature");
            }

            Payment payment = paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                    .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));

            if (payment.getStatus() == PaymentStatus.SUCCESS){
                throw new PaymentAlreadyProcessedException("Payment already processed");
            }

            if (payment.getStatus() != PaymentStatus.PENDING_PAYMENT){
                throw new InvalidPaymentStateException("Payment is not in pending state");
            }

            com.razorpay.Payment razorpayPayment = razorpayClient.payments.fetch(request.getRazorpayPaymentId());

            String paymentMethod = razorpayPayment.get("method");

            PaymentMethod methodEnum;
            try {
                methodEnum = PaymentMethod.valueOf(paymentMethod.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Unknown payment method from Razorpay: {}", paymentMethod);
                methodEnum = PaymentMethod.UNKNOWN;
            }

            String status = razorpayPayment.get("status");
            if (!"captured".equals(status)) {
                throw new PaymentVerificationException("Payment not captured");
            }

            payment.setPaymentMethod(methodEnum);

            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setTransactionId(request.getRazorpayPaymentId());

            Payment savedPayment = paymentRepository.save(payment);

            PaymentSuccessEvent event = new PaymentSuccessEvent(
                    savedPayment.getOrderId(),
                    savedPayment.getPaymentId(),
                    savedPayment.getTransactionId(),
                    savedPayment.getPaymentMethod().name(),
                    savedPayment.getStatus().name(),
                    savedPayment.getAmount()
            );

            try { // In production usually companies use outbox pattern,
                // Update payment->Insert event into OUTBOX table->
                // commit Transaction->Background process reads OUTBOX table->
                // publishes to kafka->Marks event as published
                outboxService.saveEvent(
                        "PAYMENT",
                        payment.getPaymentId(),
                        "PAYMENT_SUCCESS",
                        event
                );
            }catch (Exception ex){
                log.error("Failed to publish payment-success event for paymentId={}, error={}",
                        savedPayment.getPaymentId(), ex.getMessage(), ex);
            }

        } catch (RazorpayException e) {
            throw new PaymentVerificationException("Unable to fetch payment details from Razorpay");
        }
        return "Payment verified successfully";
    }


    @Transactional
    public String retryVerification(PaymentVerificationRequest request) {

        log.info("Retrying verification for orderId={}", request.getRazorpayOrderId());

        Payment payment = paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            return "Payment already verified";
        }

        return verifyPayment(request);
    }

}
