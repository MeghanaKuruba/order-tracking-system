package com.ordertracking.restaurant.controller;

import com.ordertracking.restaurant.dto.RestaurantRequest;
import com.ordertracking.restaurant.dto.RestaurantResponse;
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
    public ResponseEntity<List<RestaurantResponse>> getAllRestaurants() {
        return ResponseEntity.ok(restaurantService.getAllRestaurants());
    }

    @GetMapping("/getByName/{name}")
    public ResponseEntity<RestaurantResponse> getRestaurantByName(@PathVariable String name) {
        return ResponseEntity.ok(restaurantService.getRestaurantByname(name));
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<RestaurantResponse> getRestaurantById(@PathVariable long id) {
        return ResponseEntity.ok(restaurantService.getRestaurantById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<RestaurantResponse>> searchRestaurants(
            @RequestParam(required = false) String cuisineType,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String name) {
        return ResponseEntity.ok(restaurantService.searchRestaurants(cuisineType, active, name));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<RestaurantResponse> updateRestaurant(@PathVariable long id, @RequestBody RestaurantRequest request) {
        return ResponseEntity.ok(restaurantService.updateRestaurant(id, request));
    }

    @PatchMapping("/updateStatus/{id}")
    public ResponseEntity<RestaurantResponse> updateRestaurantStatus(@PathVariable long id, @RequestParam boolean active) {
        return ResponseEntity.ok(restaurantService.updateRestaurantStatus(id, active));
    }
}
