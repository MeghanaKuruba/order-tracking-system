package com.ordertracking.verification.service.impl;

import com.ordertracking.verification.dto.SubmitVerificationDocumentRequest;
import com.ordertracking.verification.dto.VerificationDocumentResponse;
import com.ordertracking.verification.entity.VerificationApplication;
import com.ordertracking.verification.entity.VerificationDocument;
import com.ordertracking.verification.exception.VerificationApplicationNotFoundException;
import com.ordertracking.verification.exception.VerificationDocumentNotFoundException;
import com.ordertracking.verification.mapper.VerificationMapper;
import com.ordertracking.verification.repository.VerificationApplicationRepository;
import com.ordertracking.verification.repository.VerificationDocumentRepository;
import com.ordertracking.verification.service.VerificationDocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationDocumentServiceImpl
        implements VerificationDocumentService {

    private final VerificationApplicationRepository applicationRepository;

    private final VerificationDocumentRepository documentRepository;

    private final VerificationMapper verificationMapper;

    @Override
    @Transactional
    public VerificationDocumentResponse submitDocument(Long applicationId,
            SubmitVerificationDocumentRequest request) {

        log.info(
                "Submitting verification document. applicationId={}, documentType={}",
                applicationId,
                request.getDocumentType()
        );

        VerificationApplication application =
                applicationRepository.findById(applicationId)
                        .orElseThrow(() -> {

                            log.warn(
                                    "Verification application not found. applicationId={}",
                                    applicationId
                            );

                            return new VerificationApplicationNotFoundException(
                                    "Verification application not found."
                            );
                        });

        VerificationDocument document =
                verificationMapper.toVerificationDocument(request, application);

        VerificationDocument saved =
                documentRepository.save(document);

        log.info(
                "Verification document submitted. documentId={}, applicationId={}, documentType={}",
                saved.getDocumentId(),
                applicationId,
                saved.getDocumentType()
        );

        return verificationMapper.toVerificationDocumentResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public VerificationDocumentResponse getDocument(
            String documentId) {

        log.debug(
                "Fetching verification document. documentId={}",
                documentId
        );

        VerificationDocument document =
                documentRepository.findById(documentId)
                        .orElseThrow(() -> {

                            log.warn(
                                    "Verification document not found. documentId={}",
                                    documentId
                            );

                            return new VerificationDocumentNotFoundException(
                                    "Verification document not found."
                            );
                        });

        return verificationMapper.toVerificationDocumentResponse(document);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VerificationDocumentResponse> getDocuments(
            Long applicationId) {

        log.debug(
                "Fetching verification documents. applicationId={}",
                applicationId
        );

        if (!applicationRepository.existsById(applicationId)) {

            log.warn(
                    "Verification application not found. applicationId={}",
                    applicationId
            );

            throw new VerificationApplicationNotFoundException(
                    "Verification application not found."
            );
        }

        List<VerificationDocument> documents =
                documentRepository
                        .findByVerificationApplicationId(applicationId);

        log.info(
                "Verification documents fetched. applicationId={}, count={}",
                applicationId,
                documents.size()
        );

        return documents
                .stream()
                .map(verificationMapper::toVerificationDocumentResponse)
                .toList();
    }
}