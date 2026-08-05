package com.ordertracking.verification.repository;

import com.ordertracking.verification.entity.VerificationApplication;
import com.ordertracking.verification.enums.ApplicantType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VerificationApplicationRepository extends JpaRepository<VerificationApplication, Long> {

    Optional<VerificationApplication> findByIdAndAuthUserId(Long id, Long authUserId);

    List<VerificationApplication> findByAuthUserId(Long authUserId);

    List<VerificationApplication> findByApplicantType(ApplicantType applicantType);

    Optional<VerificationApplication> findByReferenceIdAndApplicantType(String referenceId, ApplicantType applicantType);
}