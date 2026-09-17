package com.vibran.domain.geofence.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class UpdateGeofenceRequest {

    @Size(max = 255)    private String name;
    @Size(max = 1000)   private String description;

    @DecimalMin("-90.0")  @DecimalMax("90.0")
    private BigDecimal centerLatitude;

    @DecimalMin("-180.0") @DecimalMax("180.0")
    private BigDecimal centerLongitude;

    @Min(50) @Max(50000)
    private Integer radiusMeters;

    private Boolean alertOnEntry;
    private Boolean alertOnExit;
    private Boolean isActive;
}
