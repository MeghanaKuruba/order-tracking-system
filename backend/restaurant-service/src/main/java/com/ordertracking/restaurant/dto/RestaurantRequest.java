package com.ordertracking.restaurant.dto;

import com.ordertracking.restaurant.entity.Address;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantRequest {
    @NotNull
    private String name;
    @NotNull
    private Address address;
    private String cuisineType;
}
