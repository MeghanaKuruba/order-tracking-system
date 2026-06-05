package com.ordertracking.tracking.service.impl;

import com.ordertracking.tracking.dto.LocationResponse;
import com.ordertracking.tracking.dto.LocationUpdateRequest;
import com.ordertracking.tracking.entity.DeliveryLocation;
import com.ordertracking.tracking.exception.LocationNotFoundException;
import com.ordertracking.tracking.exception.TrackingNotFoundException;
import com.ordertracking.tracking.repository.TrackingRepository;
import com.ordertracking.tracking.service.TrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TrackingServiceImpl implements TrackingService {

    private final TrackingRepository deliveryLocationRepository;
    @Override
    public void updateLocation(LocationUpdateRequest request) {
        DeliveryLocation location = deliveryLocationRepository.findByOrderId(request.getOrderId())
                .orElseThrow(() -> new TrackingNotFoundException(
                        "Tracking record not found for orderId: " + request.getOrderId()
                ));

        // Update fields
        location.setLatitude(request.getLatitude());
        location.setLongitude(request.getLongitude());
        location.setUpdatedAt(LocalDateTime.now());

        // Save updated record
        deliveryLocationRepository.save(location);
    }


    @Override
    public LocationResponse getLocation(Long orderId) {
        DeliveryLocation location = deliveryLocationRepository.findByOrderId(orderId)
                .orElseThrow(() -> new LocationNotFoundException("Location not found for orderId: " + orderId));

        LocationResponse response = new LocationResponse();
        response.setOrderId(location.getOrderId());
        response.setDeliveryPartnerId(location.getDeliveryPartnerId());
        response.setLatitude(location.getLatitude());
        response.setLongitude(location.getLongitude());
        response.setUpdatedAt(location.getUpdatedAt());

        return response;
    }
}
