package com.ordertracking.order.dto;

import com.ordertracking.order.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private Long orderId;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
}
