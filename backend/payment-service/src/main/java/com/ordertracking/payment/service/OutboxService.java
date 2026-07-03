package com.ordertracking.payment.service;

public interface OutboxService {

    void saveEvent(String aggregateType, Long aggregateId,
                   String eventType, Object payload);
}