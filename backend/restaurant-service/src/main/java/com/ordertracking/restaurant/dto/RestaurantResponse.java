package com.ordertracking.restaurant.dto;

import com.ordertracking.restaurant.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantResponse {
    private Long id;
    private String name;
    private Address address;
    private String cuisineType;
    private boolean active;
}
