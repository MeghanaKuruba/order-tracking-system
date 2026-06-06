package com.ordertracking.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddItemRequest {
    private Long customerId;
    private Long restaurantId;
    private Long menuItemId;
    private String menuItemName;
    private Integer quantity;
    private BigDecimal price;
}
