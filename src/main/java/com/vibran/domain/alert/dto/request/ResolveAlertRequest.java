package com.vibran.domain.alert.dto.request;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResolveAlertRequest {
    @NotNull(message = "User ID is required")
    private Long userId;

    @Size(max = 1000)
    private String resolutionNotes;
}
