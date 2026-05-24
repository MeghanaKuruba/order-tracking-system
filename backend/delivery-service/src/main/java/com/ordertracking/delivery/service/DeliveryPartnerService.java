package com.ordertracking.delivery.service;

import com.ordertracking.delivery.dto.DeliveryPartnerRequest;
import com.ordertracking.delivery.dto.DeliveryPartnerResponse;

public interface DeliveryPartnerService {
    DeliveryPartnerResponse addDeliveryPartner(DeliveryPartnerRequest request);
}
