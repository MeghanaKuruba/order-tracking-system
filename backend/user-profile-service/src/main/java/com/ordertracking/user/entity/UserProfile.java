package com.ordertracking.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long authUserId;

    @Column(nullable = false)
    @NotBlank
    @Size(min = 3, max = 100)
    private String fullName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Past
    private LocalDate dateOfBirth;

    @Builder.Default
    @OneToMany(
            mappedBy = "userProfile",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<Address> addresses = new ArrayList<>();

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}