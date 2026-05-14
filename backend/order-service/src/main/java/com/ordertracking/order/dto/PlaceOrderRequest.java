package com.ordertracking.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaceOrderRequest {
    private String customerId;
    private Long restaurantId;
    private AddressRequest deliveryAddress;
    private OrderItemRequest[] orderItems;
}
