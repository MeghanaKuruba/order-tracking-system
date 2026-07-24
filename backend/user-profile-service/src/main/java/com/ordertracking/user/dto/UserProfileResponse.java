package com.ordertracking.user.dto;

import com.ordertracking.user.entity.Gender;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {

    private Long authUserId;

    private String fullName;

    private String email;

    private String phoneNumber;

    private Gender gender;

    private LocalDate dateOfBirth;

    private String profileImageUrl;

    private List<AddressResponse> addresses;
}