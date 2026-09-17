package com.vibran.domain.geofence.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateGeofenceRequest {

    @NotNull  private Long   ownerId;
    private   Long           vehicleId;   // null = all vehicles of owner

    @NotBlank @Size(max = 255)
    private String name;

    @Size(max = 1000)
    private String description;

    @NotNull
    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    private BigDecimal centerLatitude;

    @NotNull
    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    private BigDecimal centerLongitude;

    @NotNull @Min(50) @Max(50000)
    private Integer radiusMeters;

    private Boolean alertOnEntry = true;
    private Boolean alertOnExit  = true;
}
