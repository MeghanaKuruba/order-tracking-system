package com.ordertracking.restaurant.service.impl;

import com.ordertracking.restaurant.Exception.RestaurantAlreadyExistsException;
import com.ordertracking.restaurant.Exception.RestaurantNotFoundException;
import com.ordertracking.restaurant.dto.RestaurantRequest;
import com.ordertracking.restaurant.dto.RestaurantResponse;
import com.ordertracking.restaurant.entity.Restaurant;
import com.ordertracking.restaurant.repository.RestaurantRepository;
import com.ordertracking.restaurant.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
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
        return restaurantRepository.save(restaurant);
    }

    @Override
    public List<RestaurantResponse> getAllRestaurants() {
        List<Restaurant> restaurant = restaurantRepository.findAll();
        return restaurant.stream().map(this::mapToResponse).toList();
    }

    @Override
    public RestaurantResponse getRestaurantByname(String name) {
            Restaurant restaurant = restaurantRepository.findByName(name);
            if(restaurant == null) {
                throw new RestaurantNotFoundException("Restaurant not found with name: " + name);
            }

        return mapToResponse(restaurant);
    }

    @Override
    public RestaurantResponse getRestaurantById(long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found with id: " + id));
        return mapToResponse(restaurant);
    }

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

    @Override
    public RestaurantResponse updateRestaurant(long id, RestaurantRequest restaurant) {
        Restaurant existingRestaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found with id: " + id));

        existingRestaurant.setName(restaurant.getName());
        existingRestaurant.setAddress(restaurant.getAddress());
        existingRestaurant.setCuisineType(restaurant.getCuisineType());

        Restaurant updatedRestaurant = restaurantRepository.save(existingRestaurant);
        return mapToResponse(updatedRestaurant);
    }

    @Override
    public RestaurantResponse updateRestaurantStatus(long id, boolean active) {
        Restaurant existingRestaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found with id: " + id));

        existingRestaurant.setActive(active);
        Restaurant updatedRestaurant = restaurantRepository.save(existingRestaurant);
        return mapToResponse(updatedRestaurant);
    }

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
