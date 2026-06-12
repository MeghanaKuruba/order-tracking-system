package com.ordertracking.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCheckoutResponse {
    private String razorpayOrderId;
    private String keyId;
    private BigDecimal amount;
    private String currency;
}
