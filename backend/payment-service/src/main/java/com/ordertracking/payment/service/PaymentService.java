package com.ordertracking.payment.service;

import com.ordertracking.payment.dto.PaymentCheckoutResponse;
import com.ordertracking.payment.dto.PaymentHistoryResponse;
import com.ordertracking.payment.entity.Payment;
import com.ordertracking.payment.entity.PaymentStatus;
import org.springframework.data.domain.Page;

public interface PaymentService {

    Payment markPaymentAsFailed(String razorpayOrderId, String reason);
    PaymentCheckoutResponse retryPayment(Long orderId);
    PaymentHistoryResponse getPaymentHistoryByOrderId(Long orderId);

    Page<PaymentHistoryResponse> getCustomerPaymentHistory(
            Long customerId,
            PaymentStatus status,
            int page,
            int size
    );
}
