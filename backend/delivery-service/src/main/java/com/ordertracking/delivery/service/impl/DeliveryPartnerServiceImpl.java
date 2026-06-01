package com.ordertracking.delivery.service.impl;

import com.ordertracking.delivery.dto.*;
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
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DeliveryPartnerServiceImpl implements DeliveryPartnerService {

    private final DeliveryPartnerRepository deliveryPartnerRepository;

    private final DeliveryPartnerMapper deliveryPartnerMapper;

    private final DeliveryRepository deliveryRepository;

    private final DeliveryStatusProducer deliveryStatusProducer;

    private final RestTemplate restTemplate;

    private final String orderServiceUrl = "http://localhost:8082/api/orders/";

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
    public void assignDeliveryPartner(OrderReadyForPickupEvent event) {
        DeliveryPartner partner = deliveryPartnerRepository.findAll()
                .stream()
                .filter(p -> p.getActive() && p.getAvailable())
                .findFirst()
                .orElseThrow(() -> new DeliveryPartnerNotAvailableException("No delivery partner available"));

        DeliveryResponse response = restTemplate.getForObject(
                orderServiceUrl+event.getOrderId(), DeliveryResponse.class);

        partner.setAvailable(false);
        deliveryPartnerRepository.save(partner);

        Delivery delivery = Delivery.builder()
                .orderId(event.getOrderId())
                .restaurantId(event.getRestaurantId())
                .customerId(event.getCustomerId())
                .deliveryAddress(response.getDeliveryAddress())
                .restaurantAddress(event.getRestaurantAddress())
                .status(DeliveryStatus.ASSIGNED)
                .deliveryPartner(partner)
                .assignedAt(LocalDateTime.now())
                .build();

        deliveryRepository.save(delivery);

        DeliveryStatusUpdatedEvent statusEvent = DeliveryStatusUpdatedEvent.builder()
                .orderId(event.getOrderId())
                .restaurantId(event.getRestaurantId())
                .customerId(event.getCustomerId())
                .deliveryStatus(DeliveryStatus.ASSIGNED.name())
                .build();

        deliveryStatusProducer.sendDeliveryStatusUpdatedEvent(statusEvent);

        System.out.println("Assigned delivery partner " + partner.getName() + " to order " + event.getOrderId());
    }
}
