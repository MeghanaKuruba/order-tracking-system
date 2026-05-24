package com.ordertracking.delivery.dto;

import com.ordertracking.delivery.entity.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryPartnerRequest {
    private String name;
    private String phoneNumber;
    private String vehicleNumber;
    private VehicleType vehicleType;
}
