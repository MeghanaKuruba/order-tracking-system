package com.ordertracking.restaurant.service;

import com.ordertracking.restaurant.dto.MenuItemRequest;
import com.ordertracking.restaurant.dto.MenuItemResponse;
import com.ordertracking.restaurant.dto.MenuItemUpdateRequest;

import java.util.List;

public interface MenuItemService {

    MenuItemResponse addMenuItem(long restaurantId, MenuItemRequest menuItemRequest);
    List<MenuItemResponse> getMenuItemsByRestaurantId(long restaurantId);
    MenuItemResponse updateMenuItem(long menuItemId, MenuItemUpdateRequest menuItemRequest);
    void deleteMenuItem(long restaurantId, long menuItemId);
    MenuItemResponse UpdateMenuItemAvailability(long menuItemId, boolean available);
}
