package com.ordertracking.verification.service.kyc;

import com.ordertracking.verification.dto.KycVerificationRequest;
import com.ordertracking.verification.dto.KycVerificationResult;
import com.ordertracking.verification.enums.DocumentStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MockKycProvider implements KycProvider {

    @Override
    public KycVerificationResult verify(
            KycVerificationRequest request) {

        log.info(
                "Starting mock KYC verification. referenceId={}, documentType={}",
                request.getReferenceId(),
                request.getDocumentType()
        );

        /*
         * Temporary deterministic rules for development/testing.
         *
         * We deliberately use the document number to select
         * different verification scenarios.
         */

        String documentNumber =
                request.getDocumentNumber()
                        .trim()
                        .toUpperCase();

        KycVerificationResult result;

        if (documentNumber.startsWith("EXPIRED")) {

            result = KycVerificationResult.builder()
                    .status(DocumentStatus.EXPIRED)
                    .reason("Document has expired. Please upload a valid document.")
                    .build();

        } else if (documentNumber.startsWith("REUPLOAD")) {

            result = KycVerificationResult.builder()
                    .status(DocumentStatus.REUPLOAD_REQUIRED)
                    .reason("Document could not be verified. Please upload a clearer document.")
                    .build();

        } else if (documentNumber.startsWith("INVALID")) {

            result = KycVerificationResult.builder()
                    .status(DocumentStatus.INVALID)
                    .reason("Document details could not be validated.")
                    .build();

        } else if (documentNumber.startsWith("MISMATCH")) {

            result = KycVerificationResult.builder()
                    .status(DocumentStatus.MISMATCH)
                    .reason("Document information does not match the submitted application.")
                    .build();

        } else {

            result = KycVerificationResult.builder()
                    .status(DocumentStatus.VERIFIED)
                    .reason("Document verified successfully.")
                    .build();
        }

        log.info(
                "Mock KYC verification completed. referenceId={}, documentType={}, status={}",
                request.getReferenceId(),
                request.getDocumentType(),
                result.getStatus()
        );

        return result;
    }
}