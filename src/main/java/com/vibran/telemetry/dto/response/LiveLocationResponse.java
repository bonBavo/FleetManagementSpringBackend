package com.vibran.telemetry.dto.response;
import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data @Builder
public class LiveLocationResponse {
    private Long    vehicleId;
    private String  plateNumber;  // for Flutter map label
    private Double  latitude;
    private Double  longitude;
    private Double  speed;
    private Integer heading;
    private Boolean ignition;
    private Double  fuelLevel;    // fuel % or battery %
    private Instant timestamp;
}