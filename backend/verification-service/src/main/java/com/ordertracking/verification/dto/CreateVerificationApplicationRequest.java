package com.ordertracking.verification.dto;

import com.ordertracking.verification.enums.ApplicantType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateVerificationApplicationRequest {

    @NotNull
    private Long authUserId;

    @NotNull
    private ApplicantType applicantType;
}