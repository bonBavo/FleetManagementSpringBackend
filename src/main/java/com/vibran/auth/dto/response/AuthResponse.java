package com.vibran.auth.dto.response;

import com.vibran.domain.user.enums.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {

// Tokens

    private String accessToken;
    private String refreshToken;
    private long   accessTokenExpiresIn;

// User identity — Flutter stores this to avoid extra API call

    private Long     userId;
    private String   fullName;
    private String   email;
    private UserRole role;
    private String   profilePhotoUrl;
}