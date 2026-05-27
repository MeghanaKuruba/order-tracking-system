package com.ordertracking.restaurant.service.impl;

import com.ordertracking.restaurant.Exception.*;
import com.ordertracking.restaurant.dto.RestaurantAvailabilityResponse;
import com.ordertracking.restaurant.dto.RestaurantOrderStatusEvent;
import com.ordertracking.restaurant.dto.RestaurantRequest;
import com.ordertracking.restaurant.dto.RestaurantResponse;
import com.ordertracking.restaurant.entity.OrderStatus;
import com.ordertracking.restaurant.entity.Restaurant;
import com.ordertracking.restaurant.entity.RestaurantOrder;
import com.ordertracking.restaurant.kafka.producer.RestaurantOrderStatusProducer;
import com.ordertracking.restaurant.repository.RestaurantOrderRepository;
import com.ordertracking.restaurant.repository.RestaurantRepository;
import com.ordertracking.restaurant.service.RestaurantService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;

    private final RestaurantOrderRepository restaurantOrderRepository;

    private final RestaurantOrderStatusProducer producer;

    /**
     * Create a new restaurant. Throws exception if a restaurant with the same name already exists.
     * @param request
     * @return
     */
    @Override
    public Restaurant createRestaurant(RestaurantRequest request) {
        if(restaurantRepository.findByName(request.getName()) != null) {
            throw new RestaurantAlreadyExistsException("Restaurant with name " + request.getName() + " already exists");
        }
        Restaurant restaurant = new Restaurant();
        restaurant.setName(request.getName());
        restaurant.setAddress(request.getAddress());
        restaurant.setCuisineType(request.getCuisineType());
        restaurant.setActive(true);
        restaurant.setAcceptingOrders(false);
        restaurant.setOpen(false);
        return restaurantRepository.save(restaurant);
    }

    /**
     * Get all restaurants. Returns an empty list if no restaurants are found.
     * @return
     */

    @Override
    public List<RestaurantResponse> getAllRestaurants() {
        List<Restaurant> restaurant = restaurantRepository.findAll();
        return restaurant.stream().map(this::mapToResponse).toList();
    }

    /**
     * Get restaurant by name. Throws exception if restaurant not found.
     * @param name
     * @return
     */
    @Override
    public RestaurantResponse getRestaurantByname(String name) {
            Restaurant restaurant = restaurantRepository.findByName(name);
            if(restaurant == null) {
                throw new RestaurantNotFoundException("Restaurant not found with name: " + name);
            }

        return mapToResponse(restaurant);
    }

    /**
     * Get restaurant by id. Throws exception if restaurant not found.
     * @param id
     * @return
     */
    @Override
    public RestaurantResponse getRestaurantById(long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found with id: " + id));
        return mapToResponse(restaurant);
    }

    /**
     * Search restaurants by cuisine type, active status or name. If no parameters are provided, returns all restaurants. Returns an empty list if no restaurants are found.
     * @param cuisineType
     * @param active
     * @param name
     * @return
     */
    @Override
    public List<RestaurantResponse> searchRestaurants(String cuisineType, Boolean active, String name) {
        List<Restaurant> restaurants;
        if(cuisineType != null) {
            restaurants = restaurantRepository.findByCuisineTypeIgnoreCase(cuisineType);
        } else if(active != null) {
            restaurants = restaurantRepository.findByActive(active);
        } else if(name != null) {
            restaurants = restaurantRepository.findByNameContainingIgnoreCase(name);
        } else {
            restaurants = restaurantRepository.findAll();
        }
        return restaurants.stream().map(this::mapToResponse).toList();
    }

    /**
     * Update restaurant details. Only non-null and changed fields will be updated. If no changes are detected, an exception is thrown. Throws exception if restaurant not found or if a restaurant with the same name already exists.
     * @param id
     * @param restaurant
     * @return
     */
    @Override
    public RestaurantResponse updateRestaurant(long id, RestaurantRequest restaurant) {
        Restaurant existingRestaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found with id: " + id));

        boolean changed = false;

        if(restaurant.getName() != null && !restaurant.getName().isBlank()
                && !restaurant.getName().equalsIgnoreCase(existingRestaurant.getName())) {
            if(restaurantRepository.findByName(restaurant.getName()) != null) {
                throw new RestaurantAlreadyExistsException("Restaurant with name " + restaurant.getName() + " already exists");
            }
            existingRestaurant.setName(restaurant.getName());
                changed = true;
        }
        if(restaurant.getAddress() != null && !restaurant.getAddress().isBlank()
                && !restaurant.getAddress().equalsIgnoreCase(existingRestaurant.getAddress())) {
            existingRestaurant.setAddress(restaurant.getAddress());
                changed = true;
        }
        if (restaurant.getCuisineType() != null && !restaurant.getCuisineType().isBlank()
                && !restaurant.getCuisineType().equalsIgnoreCase(existingRestaurant.getCuisineType())) {
            existingRestaurant.setCuisineType(restaurant.getCuisineType());
                changed = true;
        }
        if (!changed) {
            throw new NoChangesFoundException("No changes detected. Restaurant is already up to date.");
        }

        Restaurant updatedRestaurant = restaurantRepository.save(existingRestaurant);
        return mapToResponse(updatedRestaurant);
    }

    /**
     * Activate or deactivate a restaurant. Throws exception if restaurant not found.
     * @param id
     * @param active
     * @return
     */
    @Override
    public RestaurantResponse updateRestaurantStatus(long id, boolean active) {
        Restaurant existingRestaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found with id: " + id));

        existingRestaurant.setActive(active);
        Restaurant updatedRestaurant = restaurantRepository.save(existingRestaurant);
        return mapToResponse(updatedRestaurant);
    }

    /**
     * Mark a restaurant as open. Throws exception if restaurant not found.
     * @param id
     * @return
     */
    @Override
    public Boolean openRestaurant(long id) {
        Restaurant existingRestaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found with id: " + id));

        existingRestaurant.setOpen(true);
        existingRestaurant.setAcceptingOrders(true);
        restaurantRepository.save(existingRestaurant);
        return true;
    }

    /**
     * Mark a restaurant as closed. Throws exception if restaurant not found.
     * @param id
     * @return
     */
    @Override
    public Boolean closeRestaurant(long id) {
        Restaurant existingRestaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found with id: " + id));

        existingRestaurant.setOpen(false);
        existingRestaurant.setAcceptingOrders(false);
        restaurantRepository.save(existingRestaurant);
        return true;
    }

    /**
     * Pause accepting orders for a restaurant. Throws exception if restaurant not found.
     * @param id
     * @return
     */
    @Override
    public Boolean pauseOrders(long id) {
        Restaurant existingRestaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found with id: " + id));

        existingRestaurant.setAcceptingOrders(false);
        restaurantRepository.save(existingRestaurant);
        return true;
    }

    /**
     * Resume accepting orders for a restaurant. Throws exception if restaurant not found.
     * @param id
     * @return
     */
    @Override
    public Boolean resumeOrders(long id) {
        Restaurant existingRestaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found with id: " + id));

        if (!existingRestaurant.isOpen()) {
            throw new RestaurantClosedException("Cannot resume orders for a closed restaurant. Please open the restaurant first.");
        }
        existingRestaurant.setAcceptingOrders(true);
        restaurantRepository.save(existingRestaurant);
        return true;
    }

    /**
     * Mark an order as preparing. Throws exception if restaurant or order not found.
     * @param orderId
     * @return
     */
    @Transactional
    @Override
    public String markPerparing(Long orderId) {
        RestaurantOrder order = restaurantOrderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));
        order.setStatus(OrderStatus.PREPARING);

        restaurantOrderRepository.save(order);

        RestaurantOrderStatusEvent event =
                RestaurantOrderStatusEvent.builder()
                .orderId(order.getOrderId())
                        .orderStatus("PREPARING")
                        .build();
        producer.sendRestaurantOrderStatusEvent(event);
        return "Order marked as preparing";
    }

    /**
     * Mark an order as ready for pickup. Throws exception if restaurant or order not found.
     * @param orderId
     * @return
     */
    @Transactional
    @Override
    public String markReadyForPickup(Long orderId) {
        RestaurantOrder order = restaurantOrderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));

        order.setStatus(OrderStatus.READY_FOR_PICKUP);

        restaurantOrderRepository.save(order);

        RestaurantOrderStatusEvent event =
                RestaurantOrderStatusEvent.builder()
                        .orderId(order.getOrderId())
                        .orderStatus("READY_FOR_PICKUP")
                        .build();
        producer.sendRestaurantOrderStatusEvent(event);
        return "Order marked as ready for pickup";
    }

    /**
     * Reject an order. Throws exception if restaurant or order not found.
     * @param orderId
     * @return
     */
    @Transactional
    @Override
    public String rejectOrder(Long orderId) {
        RestaurantOrder order = restaurantOrderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));

        order.setStatus(OrderStatus.REJECTED);

        restaurantOrderRepository.save(order);

        RestaurantOrderStatusEvent event =
                RestaurantOrderStatusEvent.builder()
                        .orderId(order.getOrderId())
                        .orderStatus("REJECTED")
                        .build();
        producer.sendRestaurantOrderStatusEvent(event);
        return "Order rejected";
    }

    /**
     * Get restaurant availability status (open/closed and accepting orders). Throws exception if restaurant not found.
     * @param id
     * @return
     */
    @Override
    public RestaurantAvailabilityResponse getRestaurantAvailability(long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException(
                        "Restaurant not found with id: " + id));

        return new RestaurantAvailabilityResponse(
                restaurant.isOpen(),
                restaurant.getAcceptingOrders()
        );
    }


    /**
     * Map Restaurant entity to RestaurantResponse DTO.
     * @param restaurant
     * @return
     */
    private RestaurantResponse mapToResponse(Restaurant restaurant) {
        RestaurantResponse response = new RestaurantResponse();
        response.setId(restaurant.getId());
        response.setName(restaurant.getName());
        response.setAddress(restaurant.getAddress());
        response.setCuisineType(restaurant.getCuisineType());
        response.setActive(restaurant.isActive());
        return response;
    }
}
