package com.ordertracking.payment.controller;

import com.ordertracking.payment.entity.OutboxEvent;
import com.ordertracking.payment.service.OutboxAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/outbox")
@RequiredArgsConstructor
public class OutboxAdminController {

    private final OutboxAdminService outboxAdminService;

    @GetMapping("/failed")
    public List<OutboxEvent> getFailedEvents() {

        return outboxAdminService.getFailedEvents();

    }

    @PostMapping("/{id}/retry")
    public ResponseEntity<String> retry(@PathVariable Long id) {

        outboxAdminService.replayEvent(id);

        return ResponseEntity.ok("Replay scheduled successfully.");

    }

}