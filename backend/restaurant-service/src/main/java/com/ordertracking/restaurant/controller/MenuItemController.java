package com.ordertracking.restaurant.controller;

import com.ordertracking.restaurant.dto.MenuItemRequest;
import com.ordertracking.restaurant.dto.MenuItemResponse;
import com.ordertracking.restaurant.service.MenuItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/menuItems")
public class MenuItemController {

    private final MenuItemService MenuItemService;

    @PostMapping("/menu/{restaurantId}")
    public ResponseEntity<MenuItemResponse> addMenuItem(@PathVariable long restaurantId, @RequestBody MenuItemRequest menuItemRequest) {
        MenuItemResponse response = MenuItemService.addMenuItem(restaurantId, menuItemRequest);
        return ResponseEntity.ok(response);
    }
}
