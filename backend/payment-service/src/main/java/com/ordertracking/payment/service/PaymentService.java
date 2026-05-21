package com.ordertracking.payment.service;

import com.ordertracking.payment.dto.PaymentResponse;

public interface PaymentService {
    PaymentResponse getPaymentByOrderId(Long orderId);
}
