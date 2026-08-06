package com.ordertracking.verification.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KycVerificationRequest {

    private String referenceId;

    private String documentType;

    private String documentNumber;

    private String documentUrl;
}