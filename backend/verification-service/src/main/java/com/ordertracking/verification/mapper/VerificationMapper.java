package com.ordertracking.verification.mapper;

import com.ordertracking.verification.dto.VerificationApplicationResponse;
import com.ordertracking.verification.dto.VerificationDocumentResponse;
import com.ordertracking.verification.entity.VerificationApplication;
import com.ordertracking.verification.entity.VerificationDocument;
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

    public VerificationDocumentResponse toVerificationDocumentResponse(
            VerificationDocument document) {

        return VerificationDocumentResponse.builder()
                .id(document.getId())
                .referenceId(
                        document.getVerificationApplication()
                                .getReferenceId()
                )
                .documentType(document.getDocumentType())
                .documentNumber(document.getDocumentNumber())
                .documentUrl(document.getDocumentUrl())
                .issuedAt(document.getIssuedAt())
                .expiryDate(document.getExpiryDate())
                .status(document.getStatus())
                .rejectionReason(document.getRejectionReason())
                .uploadedAt(document.getUploadedAt())
                .verifiedAt(document.getVerifiedAt())
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