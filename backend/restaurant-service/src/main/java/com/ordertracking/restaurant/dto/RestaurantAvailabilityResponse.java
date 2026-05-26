package com.ordertracking.restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantAvailabilityResponse {
    private Boolean open;
    private Boolean acceptingOrders;
}
