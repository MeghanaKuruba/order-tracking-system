package com.ordertracking.verification.mapper;

import com.ordertracking.verification.dto.VerificationApplicationResponse;
import com.ordertracking.verification.entity.VerificationApplication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class VerificationMapper {

    public VerificationApplicationResponse toVerificationAppResponse(
            VerificationApplication application) {

        return VerificationApplicationResponse.builder()
                .id(application.getId())
                .authUserId(application.getAuthUserId())
                .applicantType(application.getApplicantType())
                .referenceId(application.getReferenceId())
                .status(application.getStatus())
                .reason(application.getReason())
                .submittedAt(application.getSubmittedAt())
                .updatedAt(application.getUpdatedAt())
                .build();
    }

    public String generateReferenceId() {
        return "VER-" +
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 8)
                        .toUpperCase();
    }
}
