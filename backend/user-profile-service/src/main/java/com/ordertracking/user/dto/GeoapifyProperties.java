package com.ordertracking.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GeoapifyProperties {

    private String city;

    private String state;

    private String country;

    private String postcode;

    private Double lat;

    private Double lon;

    @JsonProperty("formatted")
    private String displayName;

}