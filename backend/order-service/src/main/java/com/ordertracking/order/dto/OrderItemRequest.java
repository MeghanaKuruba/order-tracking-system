package com.ordertracking.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemRequest {
    private Long menuItemId;
    private int quantity;
    private BigDecimal price;
}
