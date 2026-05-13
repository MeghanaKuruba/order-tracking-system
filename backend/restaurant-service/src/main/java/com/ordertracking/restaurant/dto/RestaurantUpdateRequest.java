package com.ordertracking.restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantUpdateRequest {
    private String name;
    private String address;
    private String cuisineType;
}
