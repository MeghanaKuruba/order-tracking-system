package com.ordertracking.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponse {
    private Long id;
    private Long menuItemId;
    private String menuItemName;
    private int quantity;
    private BigDecimal price;
    private BigDecimal subTotal;
}


