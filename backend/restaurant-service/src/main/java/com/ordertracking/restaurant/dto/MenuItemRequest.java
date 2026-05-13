package com.ordertracking.restaurant.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuItemRequest {
    @NotNull
    private String name;
    @NotNull
    private String description;
    @NotNull
    private double price;
    private boolean available = true; // Default value set to true
}
