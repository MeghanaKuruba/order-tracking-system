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

    private LocalDateTime searchingStartedAt;

    private final String orderServiceUrl = "http://localhost:8082/api/orders/";

    /**
     * This method is responsible for adding a new delivery partner to the system.
     * It takes a DeliveryPartnerRequest object as input, which contains the details of the delivery partner to be added.
     * The method uses a DeliveryPartnerMapper to convert the request DTO into a DeliveryPartner entity, which is then saved to the database using the DeliveryPartnerRepository.
     * Finally, it converts the saved entity back into a DeliveryPartnerResponse DTO and returns it.
     * @param request
     * @return
     */
    public DeliveryPartnerResponse addDeliveryPartner(DeliveryPartnerRequest request) {
        DeliveryPartner deliveryPartner = deliveryPartnerMapper.mapToEntity(request);
        DeliveryPartner savedPartner = deliveryPartnerRepository.save(deliveryPartner);
        return deliveryPartnerMapper.mapToResponse(savedPartner);
    }

    /**
     * This method is responsible for activating a delivery partner when they log in.
     * It takes the partner ID as input, retrieves the corresponding DeliveryPartner entity from the database, and updates its active and available status to true.
     * If the partner is not found, it throws a DeliveryPartnerNotFoundException.
     * Finally, it saves the updated partner entity back to the database and returns a success message.
     * @param partnerId
     * @return
     */
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

    /**
     * This method is responsible for deactivating a delivery partner when they log out.
     * It takes the partner ID as input, retrieves the corresponding DeliveryPartner entity from the database, and updates its active and available status to false.
     * If the partner is not found, it throws a DeliveryPartnerNotFoundException.
     * Finally, it saves the updated partner entity back to the database and returns a success message.
     * @param partnerId
     * @return
     */
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

    /**
     * This method is responsible for assigning a delivery partner to an order when it is ready for pickup.
     * It takes an OrderReadyForPickupEvent as input, which contains details about the order and the restaurant.
     * The method first retrieves additional delivery information from the Order Service using a REST call.
     * It then creates a new Delivery entity with the relevant details and saves it to the database.
     * After that, it sends a DeliveryStatusUpdatedEvent to notify other services about the new delivery status.
     * Finally, it calls the tryAssignPartner method to attempt to assign an available delivery partner to the delivery.
     * @param event
     */
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
                .searchingStartedAt(LocalDateTime.now())
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

    /**
     * This method is responsible for trying to assign an available delivery partner to a given delivery.
     * It retrieves all active and available delivery partners from the database and attempts to find one to assign to the delivery.
     * If no partners are available, it logs a message and returns without making any changes.
     * If a partner is found, it updates the partner's availability status, assigns the partner to the delivery, updates the delivery status to ASSIGNED, and saves the changes to the database.
     * Finally, it sends a DeliveryStatusUpdatedEvent to notify other services about the updated delivery status.
     * @param delivery
     */
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

    /**
     * This method is responsible for retrying the assignment of delivery partners to deliveries that are currently in the SEARCHING_FOR_PARTNER status.
     * It retrieves all deliveries with this status and attempts to assign a delivery partner to each one by calling the tryAssignPartner method.
     * This can be used as a scheduled task to periodically check for deliveries that are still waiting for a partner and try to assign them again.
     */
    @Override
    @Transactional
    public void retryPartnerAssignment() {
        List<Delivery> deliveries = deliveryRepository.findByStatus(DeliveryStatus.SEARCHING_FOR_PARTNER);
        for (Delivery delivery : deliveries) {
            tryAssignPartner(delivery);
            System.out.println("Retrying partner assignment for order " + delivery.getOrderId());
        }
    }

    /**
     * This method checks for deliveries that are currently assigned to a delivery partner or in the process of being delivered.
     * If it finds any where the assigned delivery partner is offline, it handles the situation based on the delivery status:
     * - If the delivery is still in the ASSIGNED status, it unassigns the delivery partner and sets the delivery back to SEARCHING_FOR_PARTNER.
     * - If the delivery is in PICKED_UP or OUT_FOR_DELIVERY status, it marks the delivery as DELIVERY_EXCEPTION.
     * In both cases, it sends an update event to notify other services about the change in delivery status.
     */
    @Override
    public void monitorOfflinePartners() {
        List<Delivery> deliveries = deliveryRepository.findByStatusIn(
                List.of(
                        DeliveryStatus.ASSIGNED,
                        DeliveryStatus.PICKED_UP,
                        DeliveryStatus.OUT_FOR_DELIVERY));

        for (Delivery delivery : deliveries) {

            DeliveryPartner partner = delivery.getDeliveryPartner();
            if (partner != null && !partner.getActive()) {
                System.out.println("Detected offline delivery partner " + partner.getName() + " for order " + delivery.getOrderId());

                if (delivery.getStatus() == DeliveryStatus.ASSIGNED) {

                        delivery.setDeliveryPartner(null);
                        delivery.setStatus(DeliveryStatus.SEARCHING_FOR_PARTNER);
                        delivery.setSearchingStartedAt(LocalDateTime.now());
                        deliveryRepository.save(delivery);

                        DeliveryStatusUpdatedEvent statusEvent = DeliveryStatusUpdatedEvent.builder()
                                .orderId(delivery.getOrderId())
                                .restaurantId(delivery.getRestaurantId())
                                .customerId(delivery.getCustomerId())
                                .deliveryStatus(DeliveryStatus.SEARCHING_FOR_PARTNER.name())
                                .build();

                        deliveryStatusProducer.sendDeliveryStatusUpdatedEvent(statusEvent);

                }else if (delivery.getStatus() == DeliveryStatus.PICKED_UP
                        || delivery.getStatus() == DeliveryStatus.OUT_FOR_DELIVERY) {
                        delivery.setStatus(DeliveryStatus.DELIVERY_EXCEPTION);
                        deliveryRepository.save(delivery);

                        DeliveryStatusUpdatedEvent statusEvent = DeliveryStatusUpdatedEvent.builder()
                                .orderId(delivery.getOrderId())
                                .restaurantId(delivery.getRestaurantId())
                                .customerId(delivery.getCustomerId())
                                .deliveryStatus(DeliveryStatus.DELIVERY_EXCEPTION.name())
                                .build();

                        deliveryStatusProducer.sendDeliveryStatusUpdatedEvent(statusEvent);

                }
            }
        }
    }

    /**
     * This method checks for deliveries that have been in the ASSIGNED status for more than 1 minute without being picked up.
     * If it finds any, it marks the assigned delivery partner as available again, unassigns the delivery, and sends an update event to notify other services.
     */
    @Override
    @Transactional
    public void monitorAssignedTimeouts(){
        List<Delivery> deliveries = deliveryRepository.findByStatus(DeliveryStatus.ASSIGNED);

        for (Delivery delivery : deliveries) {

            if (delivery.getAssignedAt() == null) {
                continue;
            }
            if (delivery.getAssignedAt().plusMinutes(1).isAfter(LocalDateTime.now())) {
                continue;
            }
            System.out.println("Detected assigned delivery partner timeout for order " + delivery.getOrderId());

            DeliveryPartner partner = delivery.getDeliveryPartner();

            if (partner != null) {
                partner.setAvailable(true);
                deliveryPartnerRepository.save(partner);
            }
                delivery.setDeliveryPartner(null);
                delivery.setStatus(DeliveryStatus.SEARCHING_FOR_PARTNER);
                delivery.setSearchingStartedAt(LocalDateTime.now());
                deliveryRepository.save(delivery);

                DeliveryStatusUpdatedEvent statusEvent = DeliveryStatusUpdatedEvent.builder()
                        .orderId(delivery.getOrderId())
                        .restaurantId(delivery.getRestaurantId())
                        .customerId(delivery.getCustomerId())
                        .deliveryStatus(DeliveryStatus.SEARCHING_FOR_PARTNER.name())
                        .build();

                deliveryStatusProducer.sendDeliveryStatusUpdatedEvent(statusEvent);
            System.out.println("Order " + delivery.getOrderId() + " timed out waiting for delivery partner to pick up. Reassigning...");
        }
    }


    /**
     * This method checks for deliveries that have been in the SEARCHING_FOR_PARTNER status for more than 1 minute.
     * If it finds any, it marks them as DELIVERY_EXCEPTION and sends an update event to notify other services.
     */
    @Override
    @Transactional
    public void monitorSearchingTimeouts(){
        List<Delivery> deliveries = deliveryRepository.findByStatus(DeliveryStatus.SEARCHING_FOR_PARTNER);

        for (Delivery delivery : deliveries) {

            if (delivery.getSearchingStartedAt() == null) {
                continue;
            }
            if (delivery.getSearchingStartedAt().plusMinutes(1).isAfter(LocalDateTime.now())) {
                continue;
            }
            System.out.println("Detected searching for partner timeout for order " + delivery.getOrderId());

            delivery.setStatus(DeliveryStatus.DELIVERY_EXCEPTION);
            deliveryRepository.save(delivery);

            DeliveryStatusUpdatedEvent statusEvent = DeliveryStatusUpdatedEvent.builder()
                    .orderId(delivery.getOrderId())
                    .restaurantId(delivery.getRestaurantId())
                    .customerId(delivery.getCustomerId())
                    .deliveryStatus(DeliveryStatus.DELIVERY_EXCEPTION.name())
                    .build();

            deliveryStatusProducer.sendDeliveryStatusUpdatedEvent(statusEvent);
            System.out.println("Order " + delivery.getOrderId() + " timed out searching for delivery partner. Marking as delivery exception.");
        }
    }
}