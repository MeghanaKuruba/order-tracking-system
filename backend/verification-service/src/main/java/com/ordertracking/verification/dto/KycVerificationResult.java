package com.ordertracking.verification.dto;

import com.ordertracking.verification.enums.DocumentStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KycVerificationResult {

    private DocumentStatus status;

    private String reason;
}