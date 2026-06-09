package com.ordertracking.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlaceOrderRequest {
    private Long customerId;
    private Long restaurantId;
    private AddressRequest deliveryAddress;
    private List<OrderItemRequest> orderItems;
}
