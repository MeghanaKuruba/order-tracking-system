package com.ordertracking.delivery.service.impl;

import com.ordertracking.delivery.dto.DeliveryPartnerRequest;
import com.ordertracking.delivery.dto.DeliveryPartnerResponse;
import com.ordertracking.delivery.entity.DeliveryPartner;
import com.ordertracking.delivery.mapper.DeliveryPartnerMapper;
import com.ordertracking.delivery.repository.DeliveryPartnerRepository;
import com.ordertracking.delivery.service.DeliveryPartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryPartnerServiceImpl implements DeliveryPartnerService {

    private final DeliveryPartnerRepository deliveryPartnerRepository;

    private final DeliveryPartnerMapper deliveryPartnerMapper;

    public DeliveryPartnerResponse addDeliveryPartner(DeliveryPartnerRequest request) {
        DeliveryPartner deliveryPartner = deliveryPartnerMapper.mapToEntity(request);
        DeliveryPartner savedPartner = deliveryPartnerRepository.save(deliveryPartner);
        return deliveryPartnerMapper.mapToResponse(savedPartner);
    }
}
