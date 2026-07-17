package com.ordertracking.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentHistoryResponse {

    private Long paymentId;

    private Long orderId;

    private BigDecimal amount;

    private String paymentMethod;

    private String paymentStatus;

    private String transactionId;

    private String failureReason;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}