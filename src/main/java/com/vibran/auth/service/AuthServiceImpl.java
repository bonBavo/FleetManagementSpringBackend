package com.vibran.auth.service;

import com.vibran.auth.dto.request.LoginRequest;
import com.vibran.auth.dto.request.RefreshTokenRequest;
import com.vibran.auth.dto.response.AuthResponse;
import com.vibran.auth.entity.RefreshToken;
import com.vibran.auth.repository.RefreshTokenRepository;
import com.vibran.domain.user.entity.User;
import com.vibran.domain.user.repositiory.UserRepository;
import com.vibran.shared.exception.BusinessRuleException;
import com.vibran.shared.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService{

    private final AuthenticationManager authManager;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.refresh-token-expiry-ms}")
    private long refreshTokenExpiryMs;
    @Override
    @Transactional
    public AuthResponse login(LoginRequest loginRequest, String ipAddress) {

        // 1. Authenticate via Spring Security
        //    Throws BadCredentialsException, LockedException, DisabledException
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new UnauthorizedException("Invalid email or password");
        } catch (LockedException e) {
            throw new BusinessRuleException("Account is suspended");
        } catch (DisabledException e) {
            throw new BusinessRuleException("Account is inactive");
        }

        // 2. Load user
        User user = userRepository
                .findByEmailAndIsActiveTrueAndIsDeletedFalse(loginRequest.getEmail())
                .orElseThrow(() -> new UnauthorizedException(
                        "Invalid email or password"));

        // 3. Generate tokens


        String accessToken = generateAccessToken(user);

        String rawRefreshToken = generateRawRefreshToken();
        String hashedToken = hashToken(rawRefreshToken);

        // 4. Persist refresh token
        refreshTokenRepository.save(RefreshToken.builder()
                .user(user)
                .tokenHash(hashedToken)
                .deviceInfo(loginRequest.getDeviceInfo())
                .ipAddress(ipAddress)
                .expiresAt(Instant.now()
                        .plusSeconds(refreshTokenExpiryMs / 1000))
                .build());

        log.info("Login successful: userId={}, ip={}", user.getId(), ipAddress);

        return buildAuthResponse(user, accessToken, rawRefreshToken);
    }

    // Refresh
    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request, String ipAddress) {

        String hashedToken = hashToken(request.getRefreshToken());

        // 1. Find token record
        RefreshToken storedToken = refreshTokenRepository
                .findByTokenHash(hashedToken)
                .orElseThrow(() -> new UnauthorizedException(
                        "Invalid refresh token"));

        // 2. Validate
        if (!storedToken.isValid()) {
            throw new UnauthorizedException("Refresh token has expired or been revoked");
        }

        // 3. Revoke old token (rotation — each refresh invalidates previous)
        refreshTokenRepository.revokeByTokenHash(
                hashedToken, "ROTATION", Instant.now());

        // 4. Issue new tokens
        User user = storedToken.getUser();
        UserDetails userDetails = userDetailsService
                .loadUserByUsername(user.getEmail());

        String newAccessToken = generateAccessToken(user);

        String newRaw = generateRawRefreshToken();
        String newHashed = hashToken(newRaw);

        refreshTokenRepository.save(RefreshToken.builder()
                .user(user)
                .tokenHash(newHashed)
                .deviceInfo(storedToken.getDeviceInfo())
                .ipAddress(ipAddress)
                .expiresAt(Instant.now()
                        .plusSeconds(refreshTokenExpiryMs / 1000))
                .build());

        log.info("Token refreshed: userId={}", user.getId());

        return buildAuthResponse(user, newAccessToken, newRaw);
    }

    @Transactional
    @Override
    public void logout(Long userId) {
        refreshTokenRepository.revokeAllByUserId(
                userId, "LOGOUT", Instant.now());
        log.info("User logged out: userId={}", userId);
    }


    private String generateRawRefreshToken() {
        return UUID.randomUUID().toString().replace("-", "") +
                UUID.randomUUID().toString().replace("-", "");
    }

    private String hashToken(String raw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    private AuthResponse buildAuthResponse(User user,
                                           String accessToken,
                                           String rawRefreshToken) {
        String photoUrl = (user.getProfile() != null)
                ? user.getProfile().getProfilePhotoUrl()
                : null;

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(rawRefreshToken)
                .accessTokenExpiresIn(jwtService.getAccessTokenExpiryMs())
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .profilePhotoUrl(photoUrl)
                .build();
    }

    private String generateAccessToken(User user) {
        UserDetails userDetails =
                userDetailsService.loadUserByUsername(user.getEmail());

        return jwtService.generateAccessToken(
                userDetails,
                user.getId(),
                user.getRole().name()
        );
    }
}
