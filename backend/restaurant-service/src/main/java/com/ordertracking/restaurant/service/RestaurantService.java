package com.ordertracking.restaurant.service;

import com.ordertracking.restaurant.dto.RestaurantRequest;
import com.ordertracking.restaurant.entity.Restaurant;

import java.util.List;

public interface RestaurantService {

    Restaurant createRestaurant(RestaurantRequest restaurant);

    List<Restaurant> getAllRestaurants();

    Restaurant getRestaurantByname(String name);

    long getRestaurantById(long id);
}
