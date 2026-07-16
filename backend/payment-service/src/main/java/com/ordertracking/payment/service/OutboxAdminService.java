package com.ordertracking.payment.service;

import com.ordertracking.payment.entity.OutboxEvent;

import java.util.List;

public interface OutboxAdminService {

    List<OutboxEvent> getFailedEvents();

    void replayEvent(Long eventId);

}
