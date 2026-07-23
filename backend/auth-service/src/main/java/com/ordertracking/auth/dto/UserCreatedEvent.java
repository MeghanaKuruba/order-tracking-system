package com.ordertracking.auth.dto;

import com.ordertracking.auth.entity.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreatedEvent {

    private Long authUserId;

    private String fullName;

    private String email;

    private String role;

    private String phoneNumber;
}