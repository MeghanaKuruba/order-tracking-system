package com.ordertracking.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaceOrderRequest {
    private String customerId;
    private Long restaurantId;
    private AddressRequest deliveryAddress;
    private List<OrderItemRequest> orderItems;
}
