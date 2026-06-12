package com.ordertracking.payment.service.impl;

import com.ordertracking.payment.config.razorpay.RazorpayProperties;
import com.ordertracking.payment.dto.PaymentCheckoutResponse;
import com.ordertracking.payment.dto.PaymentVerificationRequest;
import com.ordertracking.payment.entity.Payment;
import com.ordertracking.payment.entity.PaymentStatus;
import com.ordertracking.payment.exception.FailedRazorpayOrderCreation;
import com.ordertracking.payment.exception.PaymentNotFoundException;
import com.ordertracking.payment.kafka.producer.PaymentEventProducer;
import com.ordertracking.payment.repository.PaymentRepository;
import com.ordertracking.payment.service.RazorpayService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class RazorpayServiceImpl implements RazorpayService {

    private final RazorpayClient razorpayClient;

    private final PaymentRepository paymentRepository;

    private final RazorpayProperties razorpayProperties;

    private final PaymentEventProducer paymentEventProducer;

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

    @Override
    public void verifyPayment(PaymentVerificationRequest request) {
        try{
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", request.getRazorpayOrderId());
            options.put("razorpay_payment_id", request.getRazorpayPaymentId());
            options.put("razorpay_signature", request.getRazorpaySignature());

            boolean isValid = Utils.verifyPaymentSignature(options, razorpayProperties.getKeySecret());

            if(!isValid){
                throw new RuntimeException("Invalid payment signature");
            }

            Payment payment = paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                    .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));

            payment.setStatus(PaymentStatus.SUCCESS);

            payment.setTransactionId(request.getRazorpayPaymentId());

            Payment savedPayment = paymentRepository.save(payment);


        } catch (RazorpayException e) {
            throw new RuntimeException(e);
        }
    }
}
