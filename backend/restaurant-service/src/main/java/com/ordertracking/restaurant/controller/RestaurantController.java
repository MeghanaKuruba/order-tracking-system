package com.ordertracking.restaurant.controller;

import com.ordertracking.restaurant.Exception.RestaurantNotFoundException;
import com.ordertracking.restaurant.dto.RestaurantAvailabilityResponse;
import com.ordertracking.restaurant.dto.RestaurantRequest;
import com.ordertracking.restaurant.dto.RestaurantResponse;
import com.ordertracking.restaurant.entity.Restaurant;
import com.ordertracking.restaurant.repository.RestaurantRepository;
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

    private final RestaurantRepository restaurantRepository;

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

    @PatchMapping("/open/{id}")
    public ResponseEntity<Boolean> openRestaurant(@PathVariable long id) {
        return ResponseEntity.ok(restaurantService.openRestaurant(id));
    }

    @PatchMapping("/close/{id}")
    public ResponseEntity<Boolean> closeRestaurant(@PathVariable long id) {
        return ResponseEntity.ok(restaurantService.closeRestaurant(id));
    }

    @PatchMapping("/pauseOrders/{id}")
    public ResponseEntity<Boolean> pauseOrders(@PathVariable long id) {
        return ResponseEntity.ok(restaurantService.pauseOrders(id));
    }

    @PatchMapping("/resumeOrders/{id}")
    public ResponseEntity<Boolean> resumeOrders(@PathVariable long id) {
        return ResponseEntity.ok(restaurantService.resumeOrders(id));
    }

    @PutMapping("/markPreparing/{orderId}")
    public ResponseEntity<String> markPreparing(@PathVariable Long orderId) {
        return ResponseEntity.ok(restaurantService.markPerparing(orderId));
    }

    @PutMapping("/markReadyForPickup/{orderId}")
    public ResponseEntity<String> markReadyForPickup(@PathVariable Long orderId) {
        return ResponseEntity.ok(restaurantService.markReadyForPickup(orderId));
    }

    @PutMapping("/rejectOrder/{orderId}")
    public ResponseEntity<String> rejectOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(restaurantService.rejectOrder(orderId));
    }

    @GetMapping("/available/{id}")
    public ResponseEntity<RestaurantAvailabilityResponse> getAvailability(@PathVariable long id) {
        RestaurantAvailabilityResponse response = restaurantService.getRestaurantAvailability(id);
        return ResponseEntity.ok(response);
    }
}
