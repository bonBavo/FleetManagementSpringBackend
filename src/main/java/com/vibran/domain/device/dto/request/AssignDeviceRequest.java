package com.vibran.domain.device.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignDeviceRequest {
    @NotNull private Long vehicleId;
}
