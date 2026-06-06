package com.ordertracking.order.service.Impl;

import com.ordertracking.order.dto.*;
import com.ordertracking.order.entity.Order;
import com.ordertracking.order.entity.OrderItem;
import com.ordertracking.order.entity.OrderStatus;
import com.ordertracking.order.exception.*;
import com.ordertracking.order.kafka.producer.OrderEventProducer;
import com.ordertracking.order.mapper.OrderMapper;
import com.ordertracking.order.repository.OrderRepository;
import com.ordertracking.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;


@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final RestTemplate restTemplate;

    private final OrderMapper orderMapper;
    private final String MenuItemServiceUrl = "http://localhost:8080/menuItems/";
    private final String RestaurantServiceUrl = "http://localhost:8080/restaurants/";

    private final OrderEventProducer orderEventProducer;

    /**
     * Place a new order. Validates the order request, calculates total amount, and saves the order to the database. Throws exception if the order request is invalid.
     * @param request
     * @return
     */
    @Override
    public OrderResponse placeOrder(PlaceOrderRequest request) {

        if(request.getOrderItems() == null || request.getOrderItems().isEmpty()) {
            throw new InvalidOrderException("Order must contain at least one item");
        }

        if(request.getDeliveryAddress() == null) {
            throw new InvalidOrderException("Delivery address is required");
        }

        RestaurantAvailabilityResponse response = restTemplate.getForObject(
                RestaurantServiceUrl
                        + "available/"
                        + request.getRestaurantId(),
                RestaurantAvailabilityResponse.class);

        if(!response.getOpen()) {
            throw new RestaurantClosedException("Restaurant is currently unavailable");
        }

        if (!response.getAcceptingOrders()) {
            throw new RestaurantNotAcceptingOrdersException("Restaurant is temporarily not accepting orders");
        }

        // create order entity from request
        Order order = new Order();
        order.setCustomerId(request.getCustomerId());
        order.setRestaurantId(request.getRestaurantId());
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setCreatedAt(LocalDateTime.now());

        // Map AddressRequest to Address entity
        order.setDeliveryAddress(orderMapper.mapToAddressEntity(request.getDeliveryAddress()));

        // Map OrderItemRequest to OrderItem entities

        BigDecimal totalAmount = BigDecimal.ZERO;

        List<OrderItem> orderItems = new ArrayList<>();
        for(OrderItemRequest itemReq : request.getOrderItems()) {

            if(itemReq.getQuantity() <= 0) {
                throw new InvalidOrderException("Item quantity must be greater than zero");
            }

            String url = MenuItemServiceUrl + itemReq.getMenuItemId();

            System.out.println("Calling restaurant service url: " + url);

            MenuItem menuItem;

            try {
                 menuItem = restTemplate.getForObject(url, MenuItem.class);
            }catch (HttpClientErrorException e) {
                 if(e.getStatusCode().is4xxClientError()) {
                     throw new InvalidOrderException("Menu item not found with ID: " + itemReq.getMenuItemId());
                 } else {
                     throw new InvalidOrderException("Error fetching menu item with ID: " + itemReq.getMenuItemId() + ". Please try again later.");
                 }
            } catch (Exception e) {
                throw new InvalidOrderException("Restaurant service is unavailable. Please try again later.");
            }
            if (menuItem == null) {
                throw new InvalidOrderException("Menu item not found with ID: " + itemReq.getMenuItemId());
            }
            if (!menuItem.isAvailable()) {
                throw new InvalidOrderException("Menu item with ID " + itemReq.getMenuItemId() + " is not available");
            }
            if (!menuItem.getRestaurantId().equals(request.getRestaurantId())) {
                throw new InvalidOrderException("Menu item with ID " + itemReq.getMenuItemId() + " does not belong to restaurant with ID " + request.getRestaurantId());
            }

            BigDecimal itemPrice = menuItem.getPrice();
            BigDecimal itemTotalPrice = itemPrice.multiply(BigDecimal.valueOf(itemReq.getQuantity()));

            totalAmount = totalAmount.add(itemTotalPrice);

            OrderItem orderItem = orderMapper.mapToOrderItemEntity(itemReq, order, itemPrice);

            orderItems.add(orderItem);
        }

        order.setItems(orderItems);

        order.setTotalAmount(totalAmount);

        // save order to database
        Order savedOrder = orderRepository.save(order);

        OrderCreatedEvent event = orderMapper.mapToOrderCreatedEvent(savedOrder);
        orderEventProducer.sendOrderCreatedEvent(event);

        // create response DTO
        return new OrderResponse(
                savedOrder.getOrderId(),
                savedOrder.getStatus(),
                savedOrder.getTotalAmount(),
                savedOrder.getCreatedAt());
    }

    /**
     * Get order details by ID. Throws exception if order not found.
     * @param orderId
     * @return
     */
    @Override
    public OrderDetailsResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));

        return orderMapper.mapToOrderDetailsResponse(order);
    }

    /**
     * Get all orders for a specific customer. Returns an empty list if no orders are found for the customer.
     * @param customerId
     * @return
     */
    @Override
    public List<OrderSummaryResponse> getOrdersByCustomerId(Long customerId) {
        return orderRepository.findByCustomerId(customerId)
                .stream()
                .map(orderMapper::mapToOrderSummaryResponse)
                .toList();
    }

    /**
     * Update the status of an order. Validates the new status and updates the order if valid. Throws exception if order not found or if the new status is invalid.
     * @param orderId
     * @param status
     * @return
     */
    @Override
    public OrderDetailsResponse updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));

        try {
            OrderStatus newStatus = OrderStatus.valueOf(status.toUpperCase());
            order.setStatus(newStatus);
            Order updatedOrder = orderRepository.save(order);
            return orderMapper.mapToOrderDetailsResponse(updatedOrder);
        } catch (IllegalArgumentException e) {
            throw new EnumError("Invalid order status: " + status);
        }
    }

    /**
     * Cancel an order. Only orders that are in CREATED or ACCEPTED status can be cancelled. Throws exception if order not found or if the order cannot be cancelled due to its current status.
     * @param orderId
     * @return
     */
    @Override
    public OrderDetailsResponse cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));

        if (order.getStatus() == OrderStatus.PREPARING ||
                order.getStatus() == OrderStatus.PICKED_UP ||
                order.getStatus() == OrderStatus.READY_FOR_PICKUP ||
                order.getStatus() == OrderStatus.OUT_FOR_DELIVERY ||
                order.getStatus() == OrderStatus.DELIVERED) {

            throw new OrderCancellationNotAllowedException("Order cannot be cancelled at this stage. Current status: " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELLED);
        Order cancelledOrder = orderRepository.save(order);
        return orderMapper.mapToOrderDetailsResponse(cancelledOrder);
    }

    /**
     * Get all orders for a specific restaurant. Returns an empty list if no orders are found for the restaurant.
     * @param restaurantId
     * @return
     */
    @Override
    public List<OrderSummaryResponse> getOrdersByRestaurantId(Long restaurantId) {
        List<Order> orders = orderRepository.findByRestaurantId(restaurantId);

        return orders.stream()
                .map(orderMapper::mapToOrderSummaryResponse)
                .toList();
    }
}
