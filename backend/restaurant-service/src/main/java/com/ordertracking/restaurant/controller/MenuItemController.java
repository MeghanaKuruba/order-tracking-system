package com.ordertracking.restaurant.controller;

import com.ordertracking.restaurant.dto.MenuItemRequest;
import com.ordertracking.restaurant.dto.MenuItemResponse;
import com.ordertracking.restaurant.dto.MenuItemUpdateRequest;
import com.ordertracking.restaurant.service.MenuItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<MenuItemResponse>> getMenuItemsByRestaurantId(@PathVariable long restaurantId) {
        return ResponseEntity.ok(MenuItemService.getMenuItemsByRestaurantId(restaurantId));
    }

    @PutMapping("/{menuItemId}")
    public ResponseEntity<MenuItemResponse> updateMenuItem(@PathVariable long menuItemId, @RequestBody MenuItemUpdateRequest menuItemUpdateRequest) {
        MenuItemResponse response = MenuItemService.updateMenuItem(menuItemId, menuItemUpdateRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/restaurant/{restaurantId}/menuItem/{menuItemId}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable long restaurantId, @PathVariable long menuItemId) {
        MenuItemService.deleteMenuItem(restaurantId, menuItemId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{menuItemId}/availability")
    public ResponseEntity<MenuItemResponse> updateMenuItemAvailability(@PathVariable long menuItemId, @RequestParam boolean available) {
        MenuItemResponse response = MenuItemService.UpdateMenuItemAvailability(menuItemId, available);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{menuItemId}")
    public ResponseEntity<MenuItemResponse> getMenuItemById(@PathVariable long menuItemId) {
        MenuItemResponse response = MenuItemService.getMenuItemById(menuItemId);
        return ResponseEntity.ok(response);
    }
}
