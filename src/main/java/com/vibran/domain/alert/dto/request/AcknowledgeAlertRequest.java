package com.vibran.domain.alert.dto.request;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AcknowledgeAlertRequest {
    @NotNull(message = "User ID is required")
    private Long userId;
}
