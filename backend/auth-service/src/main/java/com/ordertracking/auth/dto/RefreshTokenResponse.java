package com.ordertracking.auth.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenResponse {

    private String accessToken;

    private String refreshToken;

}