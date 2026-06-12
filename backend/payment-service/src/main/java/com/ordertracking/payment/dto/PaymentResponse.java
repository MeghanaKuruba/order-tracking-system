package com.ordertracking.payment.dto;

import com.ordertracking.payment.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {
    private Long paymentId;
    private Long orderId;
    private String customerId;
    private BigDecimal amount;
    private String paymentMethod;
    private PaymentStatus status;
    private String transactionId;
    private LocalDateTime createdAt;
}
