package com.ordertracking.user.dto;

import com.ordertracking.user.entity.Gender;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProfileRequest {

    private String fullName;

    @Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = "Invalid phone number"
    )
    private String phoneNumber;

    private Gender gender;

    private LocalDate dateOfBirth;

    private String profileImageUrl;
}