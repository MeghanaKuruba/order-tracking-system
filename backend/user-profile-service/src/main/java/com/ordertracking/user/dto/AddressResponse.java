package com.ordertracking.user.dto;

import com.ordertracking.user.entity.UserProfile;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponse {

    private Long id;

    private String label;

    private String recipientName;

    private String recipientPhone;

    private String doorNoOrBuildingName;

    private String street;

    private String landmark;

    private String city;

    private String state;

    private String country;

    private String postalCode;

    private Double latitude;

    private Double longitude;

    private Boolean isDefault = false;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}