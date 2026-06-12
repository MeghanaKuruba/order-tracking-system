package com.ordertracking.payment.service;

import com.ordertracking.payment.dto.PaymentCheckoutResponse;
import com.ordertracking.payment.dto.PaymentVerificationRequest;
import com.razorpay.RazorpayClient;

import java.math.BigDecimal;

public interface RazorpayService {

    String createRazorpayOrder(Long paymentId, BigDecimal amount);
    PaymentCheckoutResponse getCheckoutDetails(Long paymentId);
    String verifyPayment(PaymentVerificationRequest request);
}
