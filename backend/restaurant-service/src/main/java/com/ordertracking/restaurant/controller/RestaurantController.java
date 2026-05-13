package com.ordertracking.restaurant.controller;

import com.ordertracking.restaurant.dto.RestaurantRequest;
import com.ordertracking.restaurant.entity.Restaurant;
import com.ordertracking.restaurant.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping("/create")
    public ResponseEntity<String> createRestaurant(@RequestBody RestaurantRequest request) {
         restaurantService.createRestaurant(request);
         return ResponseEntity.ok("Restaurant created successfully");
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {
        return ResponseEntity.ok(restaurantService.getAllRestaurants());
    }

    @GetMapping("/getByName/{name}")
    public ResponseEntity<Restaurant> getRestaurantByName(@PathVariable String name) {
        return ResponseEntity.ok(restaurantService.getRestaurantByname(name));
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<Long> getRestaurantById(@PathVariable long id) {
        return ResponseEntity.ok(restaurantService.getRestaurantById(id));
    }
}
