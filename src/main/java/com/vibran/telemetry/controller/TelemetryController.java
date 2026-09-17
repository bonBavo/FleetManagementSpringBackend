package com.vibran.telemetry.controller;


import com.vibran.shared.response.ApiResponse;
import com.vibran.telemetry.dto.response.LiveLocationResponse;
import com.vibran.telemetry.dto.response.RoutePointResponse;
import com.vibran.telemetry.dto.response.TripRouteResponse;
import com.vibran.telemetry.service.TelemetryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/telemetry")
@RequiredArgsConstructor
public class TelemetryController {

    private final TelemetryService telemetryService;

    // GET /api/v1/telemetry/{vehicleId}/position
    // Flutter: get latest GPS position for a vehicle
    @GetMapping("/{vehicleId}/position")
    public ResponseEntity<ApiResponse<LiveLocationResponse>> getLatestPosition(
            @PathVariable Long vehicleId) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        telemetryService.getLatestPosition(vehicleId)));
    }

    // GET /api/v1/telemetry/trip/{tripId}/route
    // Flutter: draw completed trip route as polyline on map
    @GetMapping("/trip/{tripId}/route")
    public ResponseEntity<ApiResponse<TripRouteResponse>> getTripRoute(
            @PathVariable Long tripId) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        telemetryService.getTripRoute(tripId)));
    }

    // GET /api/v1/telemetry/{vehicleId}/history
    //     ?from=2026-05-01T00:00:00Z&to=2026-05-01T23:59:59Z
    // Flutter: draw vehicle movement for a time window
    @GetMapping("/{vehicleId}/history")
    public ResponseEntity<ApiResponse<List<RoutePointResponse>>> getHistory(
            @PathVariable Long vehicleId,
            @RequestParam String from,
            @RequestParam String to) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        telemetryService.getVehicleHistory(vehicleId, from, to)));
    }
}
