package com.ordertracking.tracking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationResponse {
    private Long orderId;
    private Long deliveryPartnerId;
    private Double latitude;
    private Double longitude;
    private LocalDateTime updatedAt;
}
