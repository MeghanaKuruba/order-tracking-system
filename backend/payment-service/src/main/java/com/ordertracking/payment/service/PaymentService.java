package com.ordertracking.payment.service;

import com.ordertracking.payment.dto.PaymentResponse;
import com.ordertracking.payment.entity.Payment;

public interface PaymentService {
    PaymentResponse getPaymentByOrderId(Long orderId);
    Payment markPaymentAsFailed(Long paymentId, String reason);
}
