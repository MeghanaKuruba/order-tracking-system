package com.ordertracking.restaurant.repository;

import com.ordertracking.restaurant.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    Restaurant findByName(String name);
    List<Restaurant> findByCuisineTypeIgnoreCase(String cuisineType);
    List<Restaurant> findByActive(boolean active);
    List<Restaurant> findByNameContainingIgnoreCase(String name);
}
