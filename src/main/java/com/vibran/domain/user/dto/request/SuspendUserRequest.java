package com.vibran.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SuspendUserRequest {
    @NotBlank(message = "Suspension reason is required")
    @Size(max = 500)
    private String reason;
}
