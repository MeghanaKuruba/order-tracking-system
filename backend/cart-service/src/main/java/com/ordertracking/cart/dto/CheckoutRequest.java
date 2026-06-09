package com.ordertracking.cart.dto;

import com.ordertracking.cart.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckoutRequest {
    private Long customerId;
    private AddressRequest deliveryAddress;
}
