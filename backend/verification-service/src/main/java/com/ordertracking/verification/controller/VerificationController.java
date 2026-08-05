package com.ordertracking.verification.controller;

import com.ordertracking.verification.dto.CreateVerificationApplicationRequest;
import com.ordertracking.verification.dto.VerificationApplicationResponse;
import com.ordertracking.verification.service.VerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/verifications")
@RequiredArgsConstructor
public class VerificationController {

    private final VerificationService verificationService;

    @PostMapping
    public ResponseEntity<VerificationApplicationResponse> createApplication(
            @Valid @RequestBody CreateVerificationApplicationRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(verificationService.createApplication(request));
    }

    @GetMapping("/{applicationId}")
    public ResponseEntity<VerificationApplicationResponse> getApplication(
            @PathVariable Long applicationId) {

        return ResponseEntity.ok(verificationService.getApplication(applicationId));
    }

    @GetMapping("/user/{authUserId}")
    public ResponseEntity<List<VerificationApplicationResponse>> getApplicationsByUser(
            @PathVariable Long authUserId) {

        return ResponseEntity.ok(verificationService.getApplicationsByUser(authUserId));
    }
}