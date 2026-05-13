package com.ordertracking.restaurant.service.impl;

import com.ordertracking.restaurant.Exception.MenuItemAlreadyExistsException;
import com.ordertracking.restaurant.Exception.RestaurantNotFoundException;
import com.ordertracking.restaurant.dto.MenuItemRequest;
import com.ordertracking.restaurant.dto.MenuItemResponse;
import com.ordertracking.restaurant.dto.RestaurantResponse;
import com.ordertracking.restaurant.entity.MenuItem;
import com.ordertracking.restaurant.entity.Restaurant;
import com.ordertracking.restaurant.repository.MenuItemRepository;
import com.ordertracking.restaurant.repository.RestaurantRepository;
import com.ordertracking.restaurant.service.MenuItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepository;

    private final RestaurantRepository restaurantRepository;
    @Override
    public MenuItemResponse addMenuItem(long restaurantId, MenuItemRequest menuItemRequest) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found with id: " + restaurantId));
        if(menuItemRepository.existsByRestaurantIdAndNameIgnoreCase(restaurantId, menuItemRequest.getName())) {
            throw new MenuItemAlreadyExistsException("Menu item with name " + menuItemRequest.getName() + " already exists for this restaurant");
        }
        MenuItem menuItem = new MenuItem();
        menuItem.setName(menuItemRequest.getName());
        menuItem.setDescription(menuItemRequest.getDescription());
        menuItem.setPrice(menuItemRequest.getPrice());
        menuItem.setAvailable(menuItemRequest.isAvailable());
        menuItem.setRestaurant(restaurant);
        menuItemRepository.save(menuItem);
        return mapToResponse(menuItem);
    }

    private MenuItemResponse mapToResponse(MenuItem menuItem) {
        MenuItemResponse response = new MenuItemResponse();
        response.setId(menuItem.getId());
        response.setName(menuItem.getName());
        response.setDescription(menuItem.getDescription());
        response.setPrice(menuItem.getPrice());
        response.setAvailable(menuItem.isAvailable());
        response.setRestaurantId(menuItem.getRestaurant().getId());

        return response;
    }
}
