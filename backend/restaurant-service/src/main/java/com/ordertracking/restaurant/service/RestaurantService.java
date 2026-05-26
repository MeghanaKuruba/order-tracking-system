package com.ordertracking.restaurant.service;

import com.ordertracking.restaurant.dto.RestaurantAvailabilityResponse;
import com.ordertracking.restaurant.dto.RestaurantRequest;
import com.ordertracking.restaurant.dto.RestaurantResponse;
import com.ordertracking.restaurant.entity.Restaurant;

import java.util.List;

public interface RestaurantService {

    Restaurant createRestaurant(RestaurantRequest restaurant);
    List<RestaurantResponse> getAllRestaurants();
    RestaurantResponse getRestaurantByname(String name);
    RestaurantResponse getRestaurantById(long id);
    List<RestaurantResponse> searchRestaurants(String cuisineType, Boolean active, String name);
    RestaurantResponse updateRestaurant(long id, RestaurantRequest restaurant);
    RestaurantResponse updateRestaurantStatus(long id, boolean active);
    Boolean isRestaurantOpen(long id);
    Boolean isRestaurantClose(long id);
    Boolean pauseOrders(long id);
    Boolean resumeOrders(long id);
    RestaurantAvailabilityResponse getRestaurantAvailability(long id);
}
