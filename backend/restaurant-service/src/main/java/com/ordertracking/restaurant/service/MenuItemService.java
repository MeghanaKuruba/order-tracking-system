package com.ordertracking.restaurant.service;

import com.ordertracking.restaurant.dto.MenuItemRequest;
import com.ordertracking.restaurant.dto.MenuItemResponse;

public interface MenuItemService {

    MenuItemResponse addMenuItem(long restaurantId, MenuItemRequest menuItemRequest);
}
