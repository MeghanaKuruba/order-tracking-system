package com.ordertracking.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentFailureEvent {

    private Long orderId;
    private Long paymentId;
    private String status;
    private String reason;
    private BigDecimal amount;
}
