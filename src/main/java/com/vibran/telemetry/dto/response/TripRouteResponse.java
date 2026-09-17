package com.vibran.telemetry.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data @Builder
public class TripRouteResponse {
    private Long                    tripId;
    private Long                    vehicleId;
    private int                     pointCount;
    private List<RoutePointResponse> points;
}