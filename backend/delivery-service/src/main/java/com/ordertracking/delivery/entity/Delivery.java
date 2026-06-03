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

    @Column(nullable = false, unique = true)
    private Long orderId;
    private Long restaurantId;
    private String customerId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "street", column = @Column(name = "delivery_street")),
            @AttributeOverride(name = "city", column = @Column(name = "delivery_city")),
            @AttributeOverride(name = "state", column = @Column(name = "delivery_state")),
            @AttributeOverride(name = "pinCode", column = @Column(name = "delivery_pin_code")),
            @AttributeOverride(name = "country", column = @Column(name = "delivery_country"))
    })
    private Address deliveryAddress;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "street", column = @Column(name = "restaurant_street")),
            @AttributeOverride(name = "city", column = @Column(name = "restaurant_city")),
            @AttributeOverride(name = "state", column = @Column(name = "restaurant_state")),
            @AttributeOverride(name = "pinCode", column = @Column(name = "restaurant_pin_code")),
            @AttributeOverride(name = "country", column = @Column(name = "restaurant_country"))
    })
    private Address restaurantAddress;

    @ManyToOne
    @JoinColumn(name = "delivery_partner_id")
    private DeliveryPartner deliveryPartner;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;
    private LocalDateTime assignedAt;
    private LocalDateTime searchingStartedAt;
}
