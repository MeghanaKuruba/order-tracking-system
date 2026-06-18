package com.ordertracking.payment.service;

import com.ordertracking.payment.dto.PaymentCheckoutResponse;
import com.ordertracking.payment.dto.PaymentResponse;
import com.ordertracking.payment.entity.Payment;

public interface PaymentService {
    PaymentResponse getPaymentByOrderId(Long orderId);
    Payment markPaymentAsFailed(String razorpayOrderId, String reason);
    PaymentCheckoutResponse retryPayment(Long orderId);
}
