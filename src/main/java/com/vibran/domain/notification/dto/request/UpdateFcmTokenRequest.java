package com.vibran.domain.notification.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateFcmTokenRequest {
    @NotBlank(message = "FCM token is required")
    private String fcmToken;
}

