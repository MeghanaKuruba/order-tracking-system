package com.ordertracking.delivery.dto;

import com.ordertracking.delivery.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryResponse {
    private Long orderId;
    private String customerId;
    private Address deliveryAddress;
}
