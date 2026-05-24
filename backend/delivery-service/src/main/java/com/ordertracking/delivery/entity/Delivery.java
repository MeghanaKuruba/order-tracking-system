package com.ordertracking.delivery.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderId;
    private Long restaurantId;
    private String customerId;

    @Embedded
    private Address deliveryAddress;

    @ManyToOne
    @JoinColumn(name = "delivery_partner_id")
    private DeliveryPartner deliveryPartner;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;
    private LocalDateTime assignedAt;
}
