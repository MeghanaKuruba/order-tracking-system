package com.ordertracking.tracking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationUpdateMessage {
    private Long orderId;
    private Long deliveryPartnerId;
    private double latitude;
    private double longitude;
}
