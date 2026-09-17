package com.vibran.auth.controller;

import com.vibran.auth.dto.request.LoginRequest;
import com.vibran.auth.dto.request.RefreshTokenRequest;
import com.vibran.auth.dto.response.AuthResponse;
import com.vibran.auth.service.AuthService;
import com.vibran.shared.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest servletRequest) {
        
        String ipAddress = servletRequest.getRemoteAddr();
        return ResponseEntity.ok(
                ApiResponse.success("Login successful", 
                        authService.login(request, ipAddress)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @Valid @RequestBody RefreshTokenRequest request,
            HttpServletRequest servletRequest) {
        
        String ipAddress = servletRequest.getRemoteAddr();
        return ResponseEntity.ok(
                ApiResponse.success("Token refreshed", 
                        authService.refreshToken(request, ipAddress)));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestParam Long userId) {
        authService.logout(userId);
        return ResponseEntity.ok(
                ApiResponse.success("Logged out successfully", null));
    }
}
