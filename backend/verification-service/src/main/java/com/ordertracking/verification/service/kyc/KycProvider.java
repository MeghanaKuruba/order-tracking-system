package com.ordertracking.verification.service.kyc;

import com.ordertracking.verification.dto.KycVerificationRequest;
import com.ordertracking.verification.dto.KycVerificationResult;

public interface KycProvider {

    KycVerificationResult verify(KycVerificationRequest request);
}