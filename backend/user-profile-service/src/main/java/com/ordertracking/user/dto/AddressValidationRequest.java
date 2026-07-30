package com.ordertracking.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressValidationRequest {

    private String doorNoOrBuildingName;

    private String street;

    private String city;

    private String state;

    private String country;

    private String postalCode;

}
