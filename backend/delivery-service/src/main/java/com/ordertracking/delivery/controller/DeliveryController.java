package com.ordertracking.delivery.controller;
import com.ordertracking.delivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/delivery")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping("/mark-picked-up/{deliveryId}")
    public ResponseEntity<String> markPickedUp(@PathVariable Long deliveryId) {
        String response = deliveryService.markPickedUp(deliveryId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/mark-out-for-delivery/{deliveryId}")
    public ResponseEntity<String> markOutForDelivery(@PathVariable Long deliveryId) {
        String response = deliveryService.markOutForDelivery(deliveryId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/mark-delivered/{deliveryId}")
    public ResponseEntity<String> markDelivered(@PathVariable Long deliveryId) {
        String response = deliveryService.markDelivered(deliveryId);
        return ResponseEntity.ok(response);
    }
}
