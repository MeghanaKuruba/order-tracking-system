package com.ordertracking.cart.controller;

import com.ordertracking.cart.dto.AddItemRequest;
import com.ordertracking.cart.dto.CartResponse;
import com.ordertracking.cart.dto.CheckoutRequest;
import com.ordertracking.cart.dto.CheckoutResponse;
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

    /**
     * Adds an item to the cart based on the provided AddItemRequest.
     *
     * @param request The AddItemRequest containing the details of the item to be added.
     * @return A ResponseEntity with a success message if the item is added successfully.
     */
    @PostMapping("/addItems")
    public ResponseEntity<String> addItemsToCart(@Valid @RequestBody AddItemRequest request) {
        cartService.addItemToCart(request);
        return ResponseEntity.ok("Item added to cart successfully");
    }

    /**
     * Retrieves the cart details for a given customer ID.
     *
     * @param customerId The ID of the customer whose cart details are to be retrieved.
     * @return A ResponseEntity containing the CartResponse with the cart details.
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<CartResponse> getCartByCustomerId(@PathVariable Long customerId) {
        CartResponse cartResponse = cartService.getCartByCustomerId(customerId);
        return ResponseEntity.ok(cartResponse);
    }

    /**
     * Removes an item from the cart based on the provided user ID and item ID.
     *
     * @param userId The ID of the user whose cart item is to be removed.
     * @param cartItemId The ID of the cart item to be removed.
     * @return A ResponseEntity with a success message if the item is removed successfully.
     */
    @DeleteMapping("/remove")
    public ResponseEntity<String> removeItem(
            @RequestParam Long userId,
            @RequestParam Long cartItemId) {

        cartService.removeItemFromCart(userId, cartItemId);
        return ResponseEntity.ok("Item removed successfully");
    }

    /**
     * Checks out the cart based on the provided CheckoutRequest.
     *
     * @param request The CheckoutRequest containing the details for checkout.
     * @return A ResponseEntity containing the CheckoutResponse with the checkout details.
     */
    @PostMapping("/checkout")
    public CheckoutResponse checkout(@RequestBody CheckoutRequest request) {
        return ResponseEntity.ok(cartService.checkout(request)).getBody();
    }
}
