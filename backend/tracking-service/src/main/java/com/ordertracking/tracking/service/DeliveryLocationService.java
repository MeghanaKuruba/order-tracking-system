package com.ordertracking.tracking.service;

import com.ordertracking.tracking.dto.LocationResponse;
import com.ordertracking.tracking.dto.LocationUpdateRequest;

public interface DeliveryLocationService {

    void updateLocation(LocationUpdateRequest request);
    LocationResponse getLocation(Long orderId);
}
