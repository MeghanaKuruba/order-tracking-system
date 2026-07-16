package com.ordertracking.payment.repository;

import com.ordertracking.payment.entity.OutboxEvent;
import com.ordertracking.payment.entity.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    List<OutboxEvent>
    findByStatusAndLastRetryAtBeforeOrStatusAndLastRetryAtIsNull(
            OutboxStatus status1,
            LocalDateTime retryTime,
            OutboxStatus status2
    );

    List<OutboxEvent> findByStatus(OutboxStatus status);
}