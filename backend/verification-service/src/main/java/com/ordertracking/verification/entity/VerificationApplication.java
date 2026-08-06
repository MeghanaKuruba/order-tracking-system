package com.ordertracking.verification.entity;

import com.ordertracking.verification.enums.ApplicantType;
import com.ordertracking.verification.enums.VerificationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "verification_application",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_verification_reference_id",
                        columnNames = "reference_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User ID from Auth Service.
     */
    @Column(nullable = false)
    private Long authUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicantType applicantType;

    /**
     * ID of the Restaurant or DeliveryPartner
     * being verified.
     */
    @Column(name = "reference_id", nullable = false, unique = true, updatable = false)
    private String referenceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private VerificationStatus status =
            VerificationStatus.PENDING;

    private String reason;

    @Column(nullable = false, updatable = false)
    private LocalDateTime submittedAt;

    private LocalDateTime updatedAt;

    @Builder.Default
    @OneToMany(
            mappedBy = "verificationApplication",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<VerificationDocument> documents =
            new ArrayList<>();


    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();
        submittedAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}