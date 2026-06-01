package com.ordertracking.restaurant.dto;

import com.ordertracking.restaurant.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderReadyForPickupEvent {
    private Long orderId;
    private Long restaurantId;
    private String customerId;
    private Address restaurantAddress;
}
