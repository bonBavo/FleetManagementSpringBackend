package com.vibran.domain.geofence.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class GeofenceResponse {
    private Long       id;
    private Long       ownerId;
    private String     ownerName;
    private Long       vehicleId;       // null = all vehicles
    private String     vehiclePlate;
    private String     name;
    private String     description;
    private BigDecimal centerLatitude;
    private BigDecimal centerLongitude;
    private Integer    radiusMeters;
    private Boolean    alertOnEntry;
    private Boolean    alertOnExit;
    private Boolean    isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
