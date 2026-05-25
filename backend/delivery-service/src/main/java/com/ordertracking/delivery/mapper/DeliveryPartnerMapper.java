package com.ordertracking.delivery.mapper;

import com.ordertracking.delivery.dto.DeliveryPartnerRequest;
import com.ordertracking.delivery.dto.DeliveryPartnerResponse;
import com.ordertracking.delivery.entity.DeliveryPartner;
import org.springframework.stereotype.Component;

@Component
public class DeliveryPartnerMapper {
    public DeliveryPartner mapToEntity(DeliveryPartnerRequest request) {
        DeliveryPartner deliveryPartner = new DeliveryPartner();
        deliveryPartner.setName(request.getName());
        deliveryPartner.setPhoneNumber(request.getPhoneNumber());
        deliveryPartner.setVehicleNumber(request.getVehicleNumber());
        deliveryPartner.setVehicleType(request.getVehicleType());
        deliveryPartner.setActive(false); // New partners are inactive by default
        deliveryPartner.setAvailable(false); // New partners are not available until they activate their account
        return deliveryPartner;
    }

    public DeliveryPartnerResponse mapToResponse(DeliveryPartner deliveryPartner) {
        DeliveryPartnerResponse response = new DeliveryPartnerResponse();
        response.setId(deliveryPartner.getId());
        response.setName(deliveryPartner.getName());
        response.setPhoneNumber(deliveryPartner.getPhoneNumber());
        response.setVehicleNumber(deliveryPartner.getVehicleNumber());
        response.setVehicleType(deliveryPartner.getVehicleType());
        response.setActive(deliveryPartner.getActive());
        response.setAvailable(deliveryPartner.getAvailable());
        return response;
    }
}
