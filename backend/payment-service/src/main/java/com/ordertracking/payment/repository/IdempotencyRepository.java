package com.ordertracking.payment.repository;

import com.ordertracking.payment.entity.IdempotencyRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IdempotencyRepository
        extends JpaRepository<IdempotencyRecord, Long> {

    Optional<IdempotencyRecord> findByIdempotencyKey(String idempotencyKey);

}