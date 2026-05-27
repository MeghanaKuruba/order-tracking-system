package com.ordertracking.delivery.service;

import com.ordertracking.delivery.dto.DeliveryPartnerRequest;
import com.ordertracking.delivery.dto.DeliveryPartnerResponse;
import com.ordertracking.delivery.dto.RestaurantOrderStatusEvent;
import org.apache.kafka.common.protocol.types.Field;

public interface DeliveryPartnerService {
    DeliveryPartnerResponse addDeliveryPartner(DeliveryPartnerRequest request);
    String activateDeliveryPartner(Long partnerId);
    String deactivateDeliveryPartner(Long partnerId);
    void assignDeliveryPartner(RestaurantOrderStatusEvent event);
}
