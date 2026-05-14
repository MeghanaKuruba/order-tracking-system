package com.ordertracking.order.service;

import com.ordertracking.order.dto.OrderResponse;
import com.ordertracking.order.dto.PlaceOrderRequest;

public interface OrderService {
    OrderResponse placeOrder(PlaceOrderRequest request);
}
