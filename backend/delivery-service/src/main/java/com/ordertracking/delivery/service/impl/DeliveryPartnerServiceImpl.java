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
import java.util.List;
import java.util.Optional;

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

        DeliveryResponse response = restTemplate.getForObject(
                orderServiceUrl + event.getOrderId(), DeliveryResponse.class);

        Delivery delivery = Delivery.builder()
                .orderId(event.getOrderId())
                .restaurantId(event.getRestaurantId())
                .customerId(event.getCustomerId())
                .deliveryAddress(response.getDeliveryAddress())
                .restaurantAddress(event.getRestaurantAddress())
                .status(DeliveryStatus.SEARCHING_FOR_PARTNER)
                .build();

        delivery = deliveryRepository.save(delivery);

        DeliveryStatusUpdatedEvent statusEvent = DeliveryStatusUpdatedEvent.builder()
                .orderId(delivery.getOrderId())
                .restaurantId(delivery.getRestaurantId())
                .customerId(delivery.getCustomerId())
                .deliveryStatus(DeliveryStatus.SEARCHING_FOR_PARTNER.name())
                .build();

        deliveryStatusProducer.sendDeliveryStatusUpdatedEvent(statusEvent);

        tryAssignPartner(delivery);
    }

    @Override
    public void tryAssignPartner(Delivery delivery) {
        Optional<DeliveryPartner> partner = deliveryPartnerRepository.findAll()
                .stream()
                .filter(p -> p.getActive() && p.getAvailable())
                .findFirst();

        if (partner.isEmpty()){
            System.out.println("No delivery partner available for order " + delivery.getOrderId());
            return;
        }

        DeliveryPartner deliveryPartner = partner.get();

        deliveryPartner.setAvailable(false);
        deliveryPartnerRepository.save(deliveryPartner);

        delivery.setDeliveryPartner(deliveryPartner);
        delivery.setStatus(DeliveryStatus.ASSIGNED);
        delivery.setAssignedAt(LocalDateTime.now());
        deliveryRepository.save(delivery);

        DeliveryStatusUpdatedEvent statusEvent = DeliveryStatusUpdatedEvent.builder()
                .orderId(delivery.getOrderId())
                .restaurantId(delivery.getRestaurantId())
                .customerId(delivery.getCustomerId())
                .deliveryStatus(DeliveryStatus.ASSIGNED.name())
                .build();

        deliveryStatusProducer.sendDeliveryStatusUpdatedEvent(statusEvent);

        System.out.println("Assigned delivery partner " + deliveryPartner.getName() + " to order " + delivery.getOrderId());
    }

    @Override
    @Transactional
    public void retryPartnerAssignment() {
        List<Delivery> deliveries = deliveryRepository.findByStatus(DeliveryStatus.SEARCHING_FOR_PARTNER);
        for (Delivery delivery : deliveries) {
            tryAssignPartner(delivery);
            System.out.println("Retrying partner assignment for order " + delivery.getOrderId());
        }
    }
}
