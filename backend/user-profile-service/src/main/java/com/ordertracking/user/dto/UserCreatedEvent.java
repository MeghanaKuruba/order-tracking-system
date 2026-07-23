package com.ordertracking.user.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreatedEvent {

    private Long authUserId;

    private String fullName;

    private String email;

    private String phoneNumber;

    private String role;

}