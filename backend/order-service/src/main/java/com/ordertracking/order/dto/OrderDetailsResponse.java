package com.ordertracking.order.dto;

import com.ordertracking.order.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailsResponse {
    private Long orderId;
    private String customerId;
    private Long restaurantId;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private AddressResponse deliveryAddress;
    private List<OrderItemResponse> orderItems;
}
