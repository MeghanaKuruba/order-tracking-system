package com.ordertracking.order.controller;

import com.ordertracking.order.dto.OrderDetailsResponse;
import com.ordertracking.order.dto.OrderResponse;
import com.ordertracking.order.dto.OrderSummaryResponse;
import com.ordertracking.order.dto.PlaceOrderRequest;
import com.ordertracking.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(@Valid @RequestBody PlaceOrderRequest request) {
        OrderResponse response = orderService.placeOrder(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailsResponse> getOrderById(@PathVariable Long orderId) {
        OrderDetailsResponse response = orderService.getOrderById(orderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderSummaryResponse>> getOrdersByCustomerId(@PathVariable Long customerId) {
        return ResponseEntity.ok(orderService.getOrdersByCustomerId(customerId));
    }

    @PatchMapping("/update/{orderId}")
    public ResponseEntity<OrderDetailsResponse> updateOrderStatus(@PathVariable Long orderId, @RequestParam String status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status));
     }

    @PatchMapping("/cancel/{orderId}")
    public ResponseEntity<OrderDetailsResponse> cancelOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.cancelOrder(orderId));
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<OrderSummaryResponse>> getOrdersByRestaurantId(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(orderService.getOrdersByRestaurantId(restaurantId));
    }
}
