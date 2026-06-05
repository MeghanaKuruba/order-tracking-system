package com.ordertracking.tracking.repository;

import com.ordertracking.tracking.entity.DeliveryLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrackingRepository extends JpaRepository<DeliveryLocation, Long> {
    Optional<DeliveryLocation> findByOrderId(Long orderId);
}
