package com.ordertracking.payment.service.impl;

import com.ordertracking.payment.entity.OutboxEvent;
import com.ordertracking.payment.entity.OutboxStatus;
import com.ordertracking.payment.repository.OutboxEventRepository;
import com.ordertracking.payment.service.OutboxAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OutboxAdminServiceImpl implements OutboxAdminService {

    private final OutboxEventRepository repository;

    @Override
    public List<OutboxEvent> getFailedEvents() {

        return repository.findByStatus(OutboxStatus.FAILED);

    }

    @Override
    public void replayEvent(Long eventId) {

        OutboxEvent event =
                repository.findById(eventId)
                        .orElseThrow(() ->
                                new RuntimeException("Outbox Event not found"));

        event.setStatus(OutboxStatus.PENDING);
        event.setRetryCount(0);
        event.setLastRetryAt(null);
        event.setProcessedAt(null);

        repository.save(event);

    }

}