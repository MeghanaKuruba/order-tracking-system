package com.ordertracking.restaurant.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@Table(name = "addresses")
public class Address {
    private String street;
    private String city;
    private String state;
    private String pinCode;
    private String country;
}
