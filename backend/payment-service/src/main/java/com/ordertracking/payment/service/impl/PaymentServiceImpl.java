package com.ordertracking.payment.service.impl;

import com.ordertracking.payment.dto.PaymentResponse;
import com.ordertracking.payment.entity.Payment;
import com.ordertracking.payment.exception.PaymentNotFoundException;
import com.ordertracking.payment.mapper.PaymentMapper;
import com.ordertracking.payment.repository.PaymentRepository;
import com.ordertracking.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    private final PaymentMapper paymentMapper;

    @Override
    public PaymentResponse getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found for order ID: " + orderId));
        return paymentMapper.mapToPaymentResponse(payment);
    }
}
