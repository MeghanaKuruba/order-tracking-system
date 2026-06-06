package com.ordertracking.cart.service.impl;

import com.ordertracking.cart.dto.AddItemRequest;
import com.ordertracking.cart.dto.CartItemResponse;
import com.ordertracking.cart.dto.CartResponse;
import com.ordertracking.cart.entity.Cart;
import com.ordertracking.cart.entity.CartItem;
import com.ordertracking.cart.exception.CartNotFoundException;
import com.ordertracking.cart.exception.InvalidCartItemException;
import com.ordertracking.cart.exception.RestaurantMismatchException;
import com.ordertracking.cart.repository.CartRepository;
import com.ordertracking.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
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

        if (cart.getRestaurantId() != null &&
                !cart.getRestaurantId().equals(request.getRestaurantId())) {
            throw new RestaurantMismatchException(
                    "Cannot add items from different restaurant"
            );
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
    public void removeItemFromCart(Long menuItemId) {

    }
}
