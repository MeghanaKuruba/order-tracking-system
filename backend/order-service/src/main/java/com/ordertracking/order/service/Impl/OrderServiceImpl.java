package com.ordertracking.order.service.Impl;

import com.ordertracking.order.dto.AddressRequest;
import com.ordertracking.order.dto.OrderItemRequest;
import com.ordertracking.order.dto.OrderResponse;
import com.ordertracking.order.dto.PlaceOrderRequest;
import com.ordertracking.order.entity.Address;
import com.ordertracking.order.entity.Order;
import com.ordertracking.order.entity.OrderItem;
import com.ordertracking.order.entity.OrderStatus;
import com.ordertracking.order.exception.InvalidOrderException;
import com.ordertracking.order.repository.OrderRepository;
import com.ordertracking.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    @Override
    public OrderResponse placeOrder(PlaceOrderRequest request) {

        if(request.getOrderItems() == null || request.getOrderItems().length == 0) {
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
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());

        // Map AddressRequest to Address entity
        AddressRequest addrRequest = request.getDeliveryAddress();

        Address address = new Address();
        address.setStreet(addrRequest.getStreet());
        address.setCity(addrRequest.getCity());
        address.setState(addrRequest.getState());
        address.setPinCode(addrRequest.getPinCode());
        address.setCountry(addrRequest.getCountry());

        order.setDeliveryAddress(address);

        // Map OrderItemRequest to OrderItem entities and calculate total amount
        BigDecimal totalAmount = BigDecimal.ZERO;

        List<OrderItem> orderItems = Arrays.stream(request.getOrderItems()).map(itemReq -> {

            if(itemReq.getQuantity() <= 0) {
                throw new IllegalArgumentException("Item quantity must be greater than zero");
            }
            if(itemReq.getPrice().compareTo(BigDecimal.ZERO) <= 0 || itemReq.getPrice() == null) {
                throw new IllegalArgumentException("Item price must be greater than zero");
            }
            OrderItem item = new OrderItem();
            item.setMenuItemId(itemReq.getMenuItemId());
            item.setQuantity(itemReq.getQuantity());
            item.setPrice(itemReq.getPrice());

            item.setOrder(order);
            return item;
        }).toList();

        // Calculate total amount
        for (OrderItem item : orderItems){
            BigDecimal itemTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }

        order.setTotalAmount(totalAmount);
        order.setItems(orderItems);

        // save order to database
        Order savedOrder = orderRepository.save(order);

        // create response DTO
        return new OrderResponse(
                savedOrder.getId(),
                savedOrder.getStatus(),
                savedOrder.getTotalAmount(),
                savedOrder.getCreatedAt());
    }
}
