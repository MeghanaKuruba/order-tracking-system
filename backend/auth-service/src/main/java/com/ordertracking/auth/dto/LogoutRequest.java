package com.ordertracking.auth.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogoutRequest {

    private String refreshToken;

}
