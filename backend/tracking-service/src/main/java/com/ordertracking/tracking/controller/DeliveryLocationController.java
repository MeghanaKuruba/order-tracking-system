package com.ordertracking.tracking.controller;

import com.ordertracking.tracking.dto.LocationResponse;
import com.ordertracking.tracking.dto.LocationUpdateRequest;
import com.ordertracking.tracking.service.DeliveryLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/delivery-location")
@RequiredArgsConstructor
public class DeliveryLocationController {

    private final DeliveryLocationService deliveryLocationService;

    @PostMapping("/update")
    public ResponseEntity<String> updateLocation(@RequestBody LocationUpdateRequest request) {
        deliveryLocationService.updateLocation(request);
        return ResponseEntity.ok("Location updated successfully");
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<LocationResponse> getLocation(@PathVariable Long orderId) {
        return ResponseEntity.ok(deliveryLocationService.getLocation(orderId));
    }
}
