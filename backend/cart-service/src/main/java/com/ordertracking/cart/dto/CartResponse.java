package com.ordertracking.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartResponse {
    private Long id;
    private Long customerId;
    private Long restaurantId;
    private BigDecimal totalPrice;
    private List<CartItemResponse> items;
}
