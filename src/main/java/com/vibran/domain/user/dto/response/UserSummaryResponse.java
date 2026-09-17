package com.vibran.domain.user.dto.response;

import com.vibran.domain.user.enums.UserRole;
import lombok.Data;

@Data
public class UserSummaryResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private UserRole role;
    private String profilePhotoUrl;
}
