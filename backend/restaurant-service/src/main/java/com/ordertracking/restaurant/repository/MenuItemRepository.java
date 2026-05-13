package com.ordertracking.restaurant.repository;

import com.ordertracking.restaurant.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    boolean existsByRestaurantIdAndNameIgnoreCase(Long restaurantId, String name);
}
