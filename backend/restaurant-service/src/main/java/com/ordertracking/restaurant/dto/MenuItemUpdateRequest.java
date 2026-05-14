package com.ordertracking.restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuItemUpdateRequest {
    private String name;
    private String description;
    private double price;
}
