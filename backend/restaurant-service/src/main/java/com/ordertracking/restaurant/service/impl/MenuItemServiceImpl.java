package com.ordertracking.restaurant.service.impl;

import com.ordertracking.restaurant.exception.MenuItemAlreadyExistsException;
import com.ordertracking.restaurant.exception.MenuItemNotFoundException;
import com.ordertracking.restaurant.exception.NoChangesFoundException;
import com.ordertracking.restaurant.exception.RestaurantNotFoundException;
import com.ordertracking.restaurant.dto.MenuItemRequest;
import com.ordertracking.restaurant.dto.MenuItemResponse;
import com.ordertracking.restaurant.dto.MenuItemUpdateRequest;
import com.ordertracking.restaurant.entity.MenuItem;
import com.ordertracking.restaurant.entity.Restaurant;
import com.ordertracking.restaurant.repository.MenuItemRepository;
import com.ordertracking.restaurant.repository.RestaurantRepository;
import com.ordertracking.restaurant.service.MenuItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepository;

    private final RestaurantRepository restaurantRepository;

    /**
     * Add new menu item to a restaurant. Throws exception if restaurant not found or if a menu item with the same name already exists for the restaurant.
     * @param restaurantId
     * @param menuItemRequest
     * @return
     */
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

    /**
     * Get all menu items for a restaurant. Throws exception if restaurant not found.
     * @param restaurantId
     * @return
     */
    @Override
    public List<MenuItemResponse> getMenuItemsByRestaurantId(long restaurantId) {
        restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found with id: " + restaurantId));
        List<MenuItem> menuItems = menuItemRepository.findByRestaurantId(restaurantId);
        return menuItems.stream().map(this::mapToResponse).toList();

    }

    /**
     * Update menu item details. Only non-null and changed fields will be updated. If no changes are detected, an exception is thrown.
     * @param menuItemId
     * @param menuItemRequest
     * @return
     */
    @Override
    public MenuItemResponse updateMenuItem(long menuItemId, MenuItemUpdateRequest menuItemRequest) {
            MenuItem menuItem = menuItemRepository.findById(menuItemId)
                    .orElseThrow(() -> new MenuItemNotFoundException("Menu item not found with id: " + menuItemId));

            boolean changed = false;

            if(menuItemRequest.getName() != null && !menuItemRequest.getName().isBlank()
                    && !menuItemRequest.getName().equalsIgnoreCase(menuItem.getName())) {
                menuItem.setName(menuItemRequest.getName());
                changed = true;
            }
            if(menuItemRequest.getDescription() != null && !menuItemRequest.getDescription().isBlank()
                    && !menuItemRequest.getDescription().equalsIgnoreCase(menuItem.getDescription())) {
                menuItem.setDescription(menuItemRequest.getDescription());
                changed = true;
            }
            if(menuItemRequest.getPrice() > 0 && menuItemRequest.getPrice() != menuItem.getPrice()) {
                menuItem.setPrice(menuItemRequest.getPrice());
                changed = true;
            }
            if (!changed) {
                throw new NoChangesFoundException("No changes detected. Menu item is already up to date.");
            }
            MenuItem updated = menuItemRepository.save(menuItem);
            return mapToResponse(updated);
    }

    /**
     * Delete menu item by ID
     * @param restaurantId
     * @param menuItemId
     */
    @Override
    public void deleteMenuItem(long restaurantId, long menuItemId) {
        restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found with id: " + restaurantId));
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new MenuItemNotFoundException("Menu item not found with id: " + menuItemId));
        menuItemRepository.delete(menuItem);
    }

    /**
     * Update menu item availability
     * @param menuItemId
     * @param available
     * @return
     */
    @Override
    public MenuItemResponse UpdateMenuItemAvailability(long menuItemId, boolean available) {
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new MenuItemNotFoundException("Menu item not found with id: " + menuItemId));
        menuItem.setAvailable(available);
        MenuItem updated = menuItemRepository.save(menuItem);
        return mapToResponse(updated);
    }

    /**
     * Get menu item details by ID
     * @param menuItemId
     * @return
     */
    @Override
    public MenuItemResponse getMenuItemById(long menuItemId) {
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new MenuItemNotFoundException("Menu item not found with id: " + menuItemId));
        return mapToResponse(menuItem);
    }

    /**
     * Map MenuItem entity to MenuItemResponse DTO.
     * @param menuItem
     * @return
     */
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
