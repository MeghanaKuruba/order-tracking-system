package com.ordertracking.tracking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationUpdateRequest {
    private Long orderId;
    private Long deliveryPartnerId;
    private Double latitude;
    private Double longitude;
}
