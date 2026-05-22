package com.ordertracking.order.service.Impl;

import com.ordertracking.order.dto.*;
import com.ordertracking.order.entity.Address;
import com.ordertracking.order.entity.Order;
import com.ordertracking.order.entity.OrderItem;
import com.ordertracking.order.entity.OrderStatus;
import com.ordertracking.order.exception.EnumError;
import com.ordertracking.order.exception.InvalidOrderException;
import com.ordertracking.order.exception.OrderCancellationNotAllowedException;
import com.ordertracking.order.exception.OrderNotFoundException;
import com.ordertracking.order.kafka.OrderEventProducer;
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
    private final String restaurantServiceUrl = "http://localhost:8080/menuItems/";

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

        //Merge duplicate items in the order request
//        Map<Long, OrderItemRequest> mergedItems = new HashMap<>();
//
//        for (OrderItemRequest item : request.getOrderItems()) {
//
//            if (item.getMenuItemId() == null) {
//                throw new InvalidOrderException("Menu item ID cannot be null");
//            }
//            if (item.getQuantity() <= 0) {
//                throw new InvalidOrderException("Item quantity must be greater than zero");
//            }
//            if (item.getPrice() == null || item.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
//                throw new InvalidOrderException("Item price must be greater than zero");
//            }
//            if (mergedItems.containsKey(item.getMenuItemId())) {
//                OrderItemRequest existingItem = mergedItems.get(item.getMenuItemId());
//
//                if(existingItem.getPrice().compareTo(item.getPrice()) != 0) {
//                    throw new InvalidOrderException("Duplicate items must have the same price");
//                }
//                existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
//            } else {
//                mergedItems.put(item.getMenuItemId(), item);
//            }
//        }
//
//        List<OrderItemRequest> finalItems = new ArrayList<>(mergedItems.values());

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

            String url = restaurantServiceUrl + itemReq.getMenuItemId();

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
    public List<OrderSummaryResponse> getOrdersByCustomerId(String customerId) {
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

    /**
     * Accept an order. Only orders that are in CREATED status can be accepted. Throws exception if order not found or if the order cannot be accepted due to its current status.
     * @param orderId
     * @return
     */
    @Override
    public OrderDetailsResponse acceptOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));

        if (order.getStatus() != OrderStatus.CREATED) {
            throw new InvalidOrderException("Only orders in CREATED status can be accepted. Current status: " + order.getStatus());
        }

        order.setStatus(OrderStatus.ACCEPTED);
        Order acceptedOrder = orderRepository.save(order);
        return orderMapper.mapToOrderDetailsResponse(acceptedOrder);
    }

    /**
     * Reject an order. Only orders that are in CREATED status can be rejected. Throws exception if order not found or if the order cannot be rejected due to its current status.
     * @param orderId
     * @return
     */
    @Override
    public OrderDetailsResponse rejectOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));

        if (order.getStatus() != OrderStatus.CREATED) {
            throw new InvalidOrderException("Only orders in CREATED status can be rejected. Current status: " + order.getStatus());
        }

        order.setStatus(OrderStatus.REJECTED);
        Order rejectedOrder = orderRepository.save(order);
        return orderMapper.mapToOrderDetailsResponse(rejectedOrder);
    }
}
