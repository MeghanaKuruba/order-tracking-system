package com.ordertracking.cart.service;

import com.ordertracking.cart.dto.AddItemRequest;
import com.ordertracking.cart.dto.CartResponse;
import com.ordertracking.cart.dto.CheckoutRequest;
import com.ordertracking.cart.dto.CheckoutResponse;

public interface CartService {
    void addItemToCart(AddItemRequest request);
    CartResponse getCartByCustomerId(Long customerId);
    void removeItemFromCart(Long userId, Long cartItemId);
    CheckoutResponse checkout(CheckoutRequest request);
}
