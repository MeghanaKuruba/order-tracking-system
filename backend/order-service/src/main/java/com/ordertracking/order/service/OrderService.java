package com.ordertracking.order.service;

import com.ordertracking.order.dto.OrderDetailsResponse;
import com.ordertracking.order.dto.OrderResponse;
import com.ordertracking.order.dto.OrderSummaryResponse;
import com.ordertracking.order.dto.PlaceOrderRequest;
import com.ordertracking.order.entity.Order;

import java.util.List;

public interface OrderService {
    OrderResponse placeOrder(PlaceOrderRequest request);
    OrderDetailsResponse getOrderById(Long orderId);
    List<OrderSummaryResponse> getOrdersByCustomerId(String customerId);
    OrderDetailsResponse updateOrderStatus(Long orderId, String status);
    OrderDetailsResponse cancelOrder(Long orderId);
}
