package com.ordertracking.restaurant.service.impl;

import com.ordertracking.restaurant.Exception.RestaurantAlreadyExistsException;
import com.ordertracking.restaurant.Exception.RestaurantNotFoundException;
import com.ordertracking.restaurant.dto.RestaurantRequest;
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
    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    @Override
    public Restaurant getRestaurantByname(String name) {
            Restaurant restaurant = restaurantRepository.findByName(name);
            if(restaurant == null) {
                throw new RestaurantNotFoundException("Restaurant not found with name: " + name);
            }

        return restaurant;
    }

    @Override
    public long getRestaurantById(long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found with id: " + id));
        return restaurant.getId();
    }
}
