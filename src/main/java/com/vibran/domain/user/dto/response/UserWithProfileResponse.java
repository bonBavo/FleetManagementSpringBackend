package com.vibran.domain.user.dto.response;

import com.vibran.domain.user.enums.UserRole;
import lombok.Data;
import java.time.Instant;
    
@Data
public class UserWithProfileResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private UserRole role;
    private Boolean isActive;
    private Boolean isEmailVerified;
    private Instant createdAt;
    private UserProfileResponse profile;
}
