package com.ordertracking.order.mapper;

import com.ordertracking.order.dto.*;
import com.ordertracking.order.entity.Address;
import com.ordertracking.order.entity.Order;
import com.ordertracking.order.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class OrderMapper {

    public AddressResponse mapToAddressResponse(Address address) {
        if (address == null) {
            return null;
        }
        AddressResponse response = new AddressResponse();
        response.setStreet(address.getStreet());
        response.setCity(address.getCity());
        response.setState(address.getState());
        response.setPinCode(address.getPinCode());
        response.setCountry(address.getCountry());
        return response;
    }

    public OrderItemResponse mapToOrderItemResponse(OrderItem item) {
        if (item == null) {
            return null;
        }
        OrderItemResponse response = new OrderItemResponse();
        response.setId(item.getOrderItemId());
        response.setMenuItemId(item.getMenuItemId());
        response.setQuantity(item.getQuantity());
        response.setPrice(item.getPrice().toString());
        return response;
    }

    public OrderDetailsResponse mapToOrderDetailsResponse(Order order) {
        if (order == null) {
            return null;
        }
        OrderDetailsResponse response = new OrderDetailsResponse();
        response.setOrderId(order.getOrderId());
        response.setCustomerId(order.getCustomerId());
        response.setRestaurantId(order.getRestaurantId());
        response.setStatus(order.getStatus());
        response.setTotalAmount(order.getTotalAmount());
        response.setCreatedAt(order.getCreatedAt());
        response.setDeliveryAddress(mapToAddressResponse(order.getDeliveryAddress()));
        response.setOrderItems(order.getItems().stream().map(this::mapToOrderItemResponse).toList());
        return response;
    }

    public Address mapToAddressEntity(AddressRequest request) {
        if (request == null) {
            return null;
        }
        Address address = new Address();
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPinCode(request.getPinCode());
        address.setCountry(request.getCountry());
        return address;
    }

    public OrderItem mapToOrderItemEntity(OrderItemRequest request, Order order, BigDecimal price) {
        if (request == null) {
            return null;
        }
        OrderItem item = new OrderItem();
        item.setMenuItemId(request.getMenuItemId());
        item.setQuantity(request.getQuantity());
        item.setPrice(price);
        item.setOrder(order);
        return item;
    }

    public OrderSummaryResponse mapToOrderSummaryResponse(Order order) {
        if (order == null) {
            return null;
        }
        OrderSummaryResponse response = new OrderSummaryResponse();
        response.setOrderId(order.getOrderId());
        response.setRestaurantId(order.getRestaurantId());
        response.setStatus(order.getStatus());
        response.setTotalAmount(order.getTotalAmount());
        response.setCreatedAt(order.getCreatedAt());
        return response;
    }

    public OrderCreatedEvent mapToOrderCreatedEvent(Order order) {
        if (order == null) {
            return null;
        }
        return  new OrderCreatedEvent(
                order.getOrderId(),
                order.getCustomerId(),
                order.getRestaurantId(),
                order.getStatus().name(),
                order.getTotalAmount()
        );
    }
}
