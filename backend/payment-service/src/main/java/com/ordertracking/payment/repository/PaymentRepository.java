package com.ordertracking.payment.repository;

import com.ordertracking.payment.entity.Payment;
import com.ordertracking.payment.entity.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderId(Long orderId);

    Optional<Payment> findByRazorpayOrderId(String razorPayOrderId);

    Optional<Payment> findTopByOrderIdOrderByCreatedAtDesc(Long orderId);

    List<Payment> findByStatusAndUpdatedAtBefore(PaymentStatus status, LocalDateTime time);

    List<Payment> findByStatusAndRazorpayOrderIdIsNullAndRazorpayRetryCountLessThanAndLastRetryAtBefore(PaymentStatus status, Integer retryCount, LocalDateTime retryTime);

    Page<Payment> findByCustomerId(Long customerId, Pageable pageable);

    Page<Payment> findByCustomerIdAndStatus(
            Long customerId,
            PaymentStatus status,
            Pageable pageable
    );
}
