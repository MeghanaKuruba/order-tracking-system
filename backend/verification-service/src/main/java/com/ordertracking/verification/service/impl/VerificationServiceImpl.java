package com.ordertracking.verification.service.impl;

import com.ordertracking.verification.dto.CreateVerificationApplicationRequest;
import com.ordertracking.verification.dto.VerificationApplicationResponse;
import com.ordertracking.verification.entity.VerificationApplication;
import com.ordertracking.verification.exception.VerificationApplicationNotFoundException;
import com.ordertracking.verification.mapper.VerificationMapper;
import com.ordertracking.verification.repository.VerificationApplicationRepository;
import com.ordertracking.verification.service.VerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationServiceImpl
        implements VerificationService {

    private final VerificationApplicationRepository applicationRepository;

    private final VerificationMapper verificationMapper;

    @Override
    public VerificationApplicationResponse createApplication(
            CreateVerificationApplicationRequest request) {

        VerificationApplication application =
                VerificationApplication.builder()
                        .authUserId(request.getAuthUserId())
                        .applicantType(request.getApplicantType())
                        .referenceId(verificationMapper.generateReferenceId())
                        .build();

        VerificationApplication saved = applicationRepository.save(application);

        log.info(
                "Verification application created. id={}, authUserId={}, type={}, referenceId={}",
                saved.getId(),
                saved.getAuthUserId(),
                saved.getApplicantType(),
                saved.getReferenceId()
        );

        return verificationMapper.toVerificationAppResponse(saved);
    }

    @Override
    public VerificationApplicationResponse getApplication(
            Long applicationId) {

        VerificationApplication application =
                applicationRepository.findById(applicationId)
                        .orElseThrow(() ->
                                new VerificationApplicationNotFoundException(
                                        "Verification application not found."
                                )
                        );

        return verificationMapper.toVerificationAppResponse(application);
    }

    @Override
    public List<VerificationApplicationResponse>
    getApplicationsByUser(Long authUserId) {

        return applicationRepository
                .findByAuthUserId(authUserId)
                .stream()
                .map(verificationMapper::toVerificationAppResponse)
                .toList();
    }
}