package com.ordertracking.cart.controller;

import com.ordertracking.cart.dto.AddItemRequest;
import com.ordertracking.cart.dto.CartResponse;
import com.ordertracking.cart.entity.Cart;
import com.ordertracking.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    @PostMapping("/addItems")
    public ResponseEntity<String> addItemsToCart(@Valid @RequestBody AddItemRequest request) {
        cartService.addItemToCart(request);
        return ResponseEntity.ok("Item added to cart successfully");
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CartResponse> getCartByCustomerId(@PathVariable Long customerId) {
        CartResponse cartResponse = cartService.getCartByCustomerId(customerId);
        return ResponseEntity.ok(cartResponse);
    }

}
