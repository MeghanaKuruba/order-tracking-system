package com.ordertracking.cart.service.impl;

import com.ordertracking.cart.client.OrderServiceClient;
import com.ordertracking.cart.dto.*;
import com.ordertracking.cart.dto.MenuItem;
import com.ordertracking.cart.entity.Cart;
import com.ordertracking.cart.entity.CartItem;
import com.ordertracking.cart.exception.*;
import com.ordertracking.cart.repository.CartItemRepository;
import com.ordertracking.cart.repository.CartRepository;
import com.ordertracking.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    private final OrderServiceClient orderServiceClient;

    private final RestTemplate restTemplate;
    private final String RestaurantUrl = "http://localhost:8080/menuItems/";

    /**
     * Adds an item to the customer's cart. If the cart does not exist, it creates a new one.
     * Validates the request parameters and ensures that items from different restaurants cannot be added to the same cart.
     *
     * @param request The request containing details of the item to be added to the cart.
     * @throws InvalidCartItemException if any of the request parameters are invalid.
     * @throws RestaurantMismatchException if trying to add items from different restaurants to the same cart.
     */
    @Override
    public void addItemToCart(AddItemRequest request) {

        if (request.getCustomerId() == null) {
            throw new InvalidCartItemException("CustomerId cannot be null");
        }

        if (request.getMenuItemId() == null) {
            throw new InvalidCartItemException("MenuItemId cannot be null");
        }

        if (request.getQuantity() <= 0) {
            throw new InvalidCartItemException("Quantity must be greater than 0");
        }

        if (request.getPrice() == null || request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidCartItemException("Price cannot be null");
        }

        Cart cart = cartRepository.findByCustomerId(request.getCustomerId()).orElseGet(() -> {
            Cart newCart = Cart.builder()
                    .customerId(request.getCustomerId())
                    .restaurantId(request.getRestaurantId())
                    .totalAmount(BigDecimal.ZERO)
                    .build();
            return cartRepository.save(newCart);
        });

        if (cart.getRestaurantId() == null) {
            cart.setRestaurantId(request.getRestaurantId());
        }

        if (cart.getRestaurantId() != null &&
                !cart.getRestaurantId().equals(request.getRestaurantId())) {
            throw new RestaurantMismatchException(
                    "Cannot add items from different restaurant"
            );
        }
        String url = RestaurantUrl + request.getMenuItemId();

        MenuItem menuItem;

        try {
            menuItem = restTemplate.getForObject(url, MenuItem.class);
        } catch (HttpClientErrorException e) {
            throw new InvalidCartItemException("Menu item not found with ID: " + request.getMenuItemId());
        }

        if (menuItem == null) {
            throw new InvalidCartItemException("Menu item not found");
        }

        if (!menuItem.isAvailable()) {
            throw new InvalidCartItemException("Menu item is not available");
        }

        if (!menuItem.getRestaurantId().equals(request.getRestaurantId())) {
            throw new InvalidCartItemException("Item does not belong to selected restaurant");
        }

        if (cart.getCartItems() == null) {
            cart.setCartItems(new ArrayList<>());
        }

        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getMenuItemId().equals(request.getMenuItemId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            item.setSubTotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        } else {
            CartItem newItem = CartItem.builder()
                    .menuItemId(request.getMenuItemId())
                    .itemName(request.getMenuItemName())
                    .quantity(request.getQuantity())
                    .price(request.getPrice())
                    .subTotal(request.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())))
                    .cart(cart)
                    .build();
            cart.getCartItems().add(newItem);
        }

        BigDecimal totalAmount = cart.getCartItems().stream()
                .map(CartItem::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalAmount(totalAmount);
        cartRepository.save(cart);
    }

    /**
     * Retrieves the cart details for a given customer ID. If the cart does not exist, it throws a CartNotFoundException.
     *
     * @param customerId The ID of the customer whose cart is to be retrieved.
     * @return A CartResponse object containing the cart details.
     * @throws CartNotFoundException if no cart is found for the given customer ID.
     */
    @Override
    public CartResponse getCartByCustomerId(Long customerId) {
            Cart cart = cartRepository.findByCustomerId(customerId)
                    .orElseThrow(() -> new CartNotFoundException("Cart not found for customerId: " + customerId));

            CartResponse response = new CartResponse();
            response.setId(cart.getId());
            response.setCustomerId(cart.getCustomerId());
            response.setRestaurantId(cart.getRestaurantId());
            response.setTotalPrice(cart.getTotalAmount());

            if (cart.getCartItems() != null) {
                response.setItems(cart.getCartItems().stream().map(item -> {
                    return new CartItemResponse(
                            item.getId(),
                            item.getMenuItemId(),
                            item.getItemName(),
                            item.getQuantity(),
                            item.getPrice(),
                            item.getSubTotal()
                    );
                }).toList());
            }

            return response;
    }

    @Override
    public void removeItemFromCart(Long userId, Long itemId) {

        // 1. Fetch cart
        Cart cart = cartRepository.findByCustomerId(userId)
                .orElseThrow(() ->
                        new CartNotFoundException("Cart not found for user: " + userId));

        // 2. Find item in cart
        CartItem item = cart.getCartItems().stream()
                .filter(ci -> ci.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() ->
                        new CartItemNotFoundException("Item not found in cart"));

        // 3. Remove item
        cart.getCartItems().remove(item);

        // 4. If cart becomes empty → reset restaurantId
        if (cart.getCartItems().isEmpty()) {
            cart.setRestaurantId(null);
        }

        // 5. Recalculate total price
        BigDecimal total = cart.getCartItems().stream()
                .map(CartItem::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        cart.setTotalAmount(total);

        // 6. Save cart
        cartRepository.save(cart);
    }

    @Override
    public CheckoutResponse checkout(CheckoutRequest request) {

        // Fetch cart
        Cart cart = cartRepository.findByCustomerId(request.getCustomerId())
                .orElseThrow(() ->
                        new CartNotFoundException("Cart not found for customerId: " + request.getCustomerId()));

        // Validate cart
        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new EmptyCartException("Cart is empty for customerId: " + request.getCustomerId());
        }

        // Convert CartItems → OrderItemRequest
        List<OrderItemRequest> items = cart.getCartItems().stream()
                .map(item -> new OrderItemRequest(
                        item.getMenuItemId(),
                        item.getQuantity()
                ))
                .toList();

        // Build request for Order Service
        PlaceOrderRequest orderRequest = PlaceOrderRequest.builder()
                .customerId(cart.getCustomerId())
                .restaurantId(cart.getRestaurantId())
                .deliveryAddress(request.getDeliveryAddress())
                .orderItems(items)
                .build();

        OrderResponse orderResponse;

        // Call Order Service
        try {
            orderResponse = orderServiceClient.placeOrder(orderRequest);

        } catch (HttpClientErrorException e) {
            throw new OrderServiceException("Order failed. Please try again.");
        }

        // Clear cart after success
        cart.getCartItems().clear();
        cart.setRestaurantId(null);
        cart.setTotalAmount(null);
        cartRepository.save(cart);

        // Return full response
        return new CheckoutResponse(
                orderResponse.getOrderId(),
                orderResponse.getStatus(),
                orderResponse.getTotalAmount(),
                orderResponse.getCreatedAt(),
                "Order placed successfully"
        );
    }
}
