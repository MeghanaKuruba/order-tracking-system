package com.ordertracking.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentSuccessEvent {
    private Long orderId;
    private String paymentId;
    private String transactionId;
    private String status;
    private BigDecimal totalAmount;
}
