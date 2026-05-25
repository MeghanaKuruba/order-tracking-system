package com.ordertracking.delivery.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class DeliveryPartner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String phoneNumber;
    private String VehicleNumber;

    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;
    private Boolean active;
    private Boolean available;
}
