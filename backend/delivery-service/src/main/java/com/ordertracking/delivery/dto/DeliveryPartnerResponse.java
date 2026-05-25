package com.ordertracking.delivery.dto;

import com.ordertracking.delivery.entity.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryPartnerResponse {
    private Long id;
    private String name;
    private String phoneNumber;
    private String vehicleNumber;
    private VehicleType vehicleType;
    private Boolean active;
    private Boolean available;
}
