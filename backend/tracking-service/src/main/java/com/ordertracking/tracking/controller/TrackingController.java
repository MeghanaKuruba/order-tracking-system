package com.ordertracking.tracking.controller;

import com.ordertracking.tracking.dto.LocationResponse;
import com.ordertracking.tracking.dto.LocationUpdateRequest;
import com.ordertracking.tracking.service.TrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/delivery-location")
@RequiredArgsConstructor
public class TrackingController {

    private final TrackingService trackingService;

    @PostMapping("/update")
    public ResponseEntity<String> updateLocation(@RequestBody LocationUpdateRequest request) {
        trackingService.updateLocation(request);
        return ResponseEntity.ok("Location updated successfully");
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<LocationResponse> getLocation(@PathVariable Long orderId) {
        return ResponseEntity.ok(trackingService.getLocation(orderId));
    }
}
