package com.ordertracking.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestaurantOrderStatusEvent {
    private Long orderId;
    private Long restaurantId;
    private Long customerId;
    private String orderStatus;
}
