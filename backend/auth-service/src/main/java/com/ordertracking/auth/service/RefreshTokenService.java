package com.ordertracking.auth.service;

import com.ordertracking.auth.entity.RefreshToken;
import com.ordertracking.auth.entity.User;

public interface RefreshTokenService {

    RefreshToken issueRefreshToken(User user);

    RefreshToken verifyRefreshToken(String token);

    void revokeRefreshToken(String token);

}