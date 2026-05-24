package com.ordertracking.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderReadyForPickupEvent {
    private Long orderId;
    private Long restaurantId;
    private String customerId;
    private String status;
}
