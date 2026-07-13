package com.ordertracking.payment.dto;

import com.ordertracking.payment.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentEvent {

    private Long orderId;

    private Long paymentId;

    private String paymentStatus;

    private BigDecimal amount;

    private String transactionId;

    private String paymentMethod;

    private String failureReason;
}