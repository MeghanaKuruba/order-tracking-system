package com.ordertracking.order.dto;

import com.ordertracking.order.entity.Address;
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
