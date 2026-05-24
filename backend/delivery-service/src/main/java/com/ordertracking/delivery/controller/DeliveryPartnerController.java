package com.ordertracking.delivery.controller;

import com.ordertracking.delivery.dto.DeliveryPartnerRequest;
import com.ordertracking.delivery.dto.DeliveryPartnerResponse;
import com.ordertracking.delivery.service.DeliveryPartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/delivery-partners")
public class DeliveryPartnerController {

    private final DeliveryPartnerService deliveryPartnerService;

    @PostMapping
    public ResponseEntity<DeliveryPartnerResponse> addDeliveryPartner(@RequestBody DeliveryPartnerRequest request) {
        DeliveryPartnerResponse response = deliveryPartnerService.addDeliveryPartner(request);
        return ResponseEntity.ok(response);
    }
}
