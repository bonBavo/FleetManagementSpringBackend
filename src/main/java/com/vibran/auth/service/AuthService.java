package com.vibran.auth.service;

import com.vibran.auth.dto.request.LoginRequest;
import com.vibran.auth.dto.request.RefreshTokenRequest;
import com.vibran.auth.dto.response.AuthResponse;
import org.springframework.transaction.annotation.Transactional;


public interface AuthService {

    AuthResponse login(LoginRequest loginRequest, String ipAddress);
    AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest, String ipAddress);
    @Transactional
    void logout(Long userId);
}
