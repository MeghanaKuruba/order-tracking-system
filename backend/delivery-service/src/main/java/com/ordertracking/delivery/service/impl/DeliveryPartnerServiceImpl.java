package com.ordertracking.delivery.service.impl;

import com.ordertracking.delivery.dto.DeliveryPartnerRequest;
import com.ordertracking.delivery.dto.DeliveryPartnerResponse;
import com.ordertracking.delivery.dto.DeliveryStatusUpdatedEvent;
import com.ordertracking.delivery.dto.RestaurantOrderStatusEvent;
import com.ordertracking.delivery.entity.Delivery;
import com.ordertracking.delivery.entity.DeliveryPartner;
import com.ordertracking.delivery.entity.DeliveryStatus;
import com.ordertracking.delivery.exception.DeliveryPartnerNotAvailableException;
import com.ordertracking.delivery.exception.DeliveryPartnerNotFoundException;
import com.ordertracking.delivery.kafka.DeliveryStatusProducer;
import com.ordertracking.delivery.mapper.DeliveryPartnerMapper;
import com.ordertracking.delivery.repository.DeliveryPartnerRepository;
import com.ordertracking.delivery.repository.DeliveryRepository;
import com.ordertracking.delivery.service.DeliveryPartnerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DeliveryPartnerServiceImpl implements DeliveryPartnerService {

    private final DeliveryPartnerRepository deliveryPartnerRepository;

    private final DeliveryPartnerMapper deliveryPartnerMapper;

    private final DeliveryRepository deliveryRepository;

    private final DeliveryStatusProducer deliveryStatusProducer;

    public DeliveryPartnerResponse addDeliveryPartner(DeliveryPartnerRequest request) {
        DeliveryPartner deliveryPartner = deliveryPartnerMapper.mapToEntity(request);
        DeliveryPartner savedPartner = deliveryPartnerRepository.save(deliveryPartner);
        return deliveryPartnerMapper.mapToResponse(savedPartner);
    }

    @Override
    @Transactional
    public String activateDeliveryPartner(Long partnerId) {
        DeliveryPartner partner = deliveryPartnerRepository.findById(partnerId)
                .orElseThrow(() -> new DeliveryPartnerNotFoundException("Delivery Partner not found with ID: " + partnerId));
        partner.setActive(true);
        partner.setAvailable(true);
        deliveryPartnerRepository.save(partner);
        return "Delivery Partner logged in successfully";
    }

    @Override
    @Transactional
    public String deactivateDeliveryPartner(Long partnerId) {
        DeliveryPartner partner = deliveryPartnerRepository.findById(partnerId)
                .orElseThrow(() -> new DeliveryPartnerNotFoundException("Delivery Partner not found with ID: " + partnerId));
        partner.setActive(false);
        partner.setAvailable(false);
        deliveryPartnerRepository.save(partner);
        return "Delivery Partner logged out successfully";
    }

    @Override
    @Transactional
    public void assignDeliveryPartner(RestaurantOrderStatusEvent event) {
        DeliveryPartner partner = deliveryPartnerRepository.findAll()
                .stream()
                .filter(p -> p.getActive() && p.getAvailable())
                .findFirst()
                .orElseThrow(() -> new DeliveryPartnerNotAvailableException("No delivery partner available"));
        partner.setAvailable(false);
        deliveryPartnerRepository.save(partner);

        Delivery delivery = Delivery.builder()
                .orderId(event.getOrderId())
                .restaurantId(event.getRestaurantId())
                .customerId(event.getCustomerId())
                .status(DeliveryStatus.ASSIGNED)
                .deliveryPartner(partner)
                .assignedAt(LocalDateTime.now())
                .build();
        deliveryRepository.save(delivery);
        DeliveryStatusUpdatedEvent event1 = DeliveryStatusUpdatedEvent.builder()
                        .orderId(delivery.getOrderId())
                                .deliveryStatus("ASSIGNED")
                                        .build();

        deliveryStatusProducer.sendDeliveryStatusUpdatedEvent(event1);

        System.out.println("Assigned delivery partner " + partner.getName() + " to order " + event.getOrderId());
    }
}
