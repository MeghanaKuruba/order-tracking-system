package com.ordertracking.delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryStatusUpdatedEvent {
    private Long orderId;
    private String deliveryStatus;
}
