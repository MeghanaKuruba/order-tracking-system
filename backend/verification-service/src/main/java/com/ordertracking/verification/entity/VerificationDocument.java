package com.ordertracking.verification.entity;

import com.ordertracking.verification.enums.DocumentStatus;
import com.ordertracking.verification.enums.DocumentType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "verification_documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "verification_application_id",
            nullable = false
    )
    private VerificationApplication verificationApplication;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentType documentType;

    private String documentNumber;

    /**
     * Location of the uploaded document.
     *
     * For now this can be a local path.
     * Later this can point to object storage.
     */
    private String documentUrl;

    private LocalDate issuedAt;

    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private DocumentStatus status =
            DocumentStatus.UPLOADED;

    private String rejectionReason;

    private LocalDateTime uploadedAt;

    private LocalDateTime verifiedAt;

    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }
}