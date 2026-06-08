package com.ordertracking.cart.service;

import com.ordertracking.cart.dto.AddItemRequest;
import com.ordertracking.cart.dto.CartResponse;

public interface CartService {
    void addItemToCart(AddItemRequest request);
    CartResponse getCartByCustomerId(Long customerId);
    void removeItemFromCart(Long userId, Long cartItemId);
}
