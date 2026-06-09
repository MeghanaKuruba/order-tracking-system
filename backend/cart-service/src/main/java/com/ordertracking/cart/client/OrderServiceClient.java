package com.ordertracking.cart.client;

import com.ordertracking.cart.dto.PlaceOrderRequest;
import com.ordertracking.cart.dto.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class OrderServiceClient {

    private final RestTemplate restTemplate;

    public OrderResponse placeOrder(PlaceOrderRequest request) {
        return restTemplate.postForObject(
                "http://localhost:8082/api/orders",
                request,
                OrderResponse.class
        );
    }
}