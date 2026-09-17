package com.vibran.telemetry.dto.response;


import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data @Builder
public class RoutePointResponse {
    private Double  latitude;
    private Double  longitude;
    private Double  speed;
    private Instant timestamp;
}