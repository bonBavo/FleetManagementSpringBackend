package com.vibran.domain.geofence.controller;

import com.vibran.domain.geofence.dto.request.CreateGeofenceRequest;
import com.vibran.domain.geofence.dto.request.UpdateGeofenceRequest;
import com.vibran.domain.geofence.dto.response.GeofenceResponse;
import com.vibran.domain.geofence.service.GeofenceService;
import com.vibran.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/geofences")
@RequiredArgsConstructor
public class GeofenceController {

    private final GeofenceService geofenceService;

    // POST /api/v1/geofences
    @PostMapping
    public ResponseEntity<ApiResponse<GeofenceResponse>> create(
            @Valid @RequestBody CreateGeofenceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Geofence created",
                geofenceService.create(request)));
    }

    // GET /api/v1/geofences/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GeofenceResponse>> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
            ApiResponse.success(geofenceService.getById(id)));
    }

    // PUT /api/v1/geofences/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<GeofenceResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateGeofenceRequest request) {
        return ResponseEntity.ok(
            ApiResponse.success("Geofence updated",
                geofenceService.update(id, request)));
    }

    // POST /api/v1/geofences/{id}/deactivate
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivate(
            @PathVariable Long id) {
        geofenceService.deactivate(id);
        return ResponseEntity.ok(
            ApiResponse.success("Geofence deactivated", null));
    }

    // DELETE /api/v1/geofences/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id) {
        geofenceService.delete(id);
        return ResponseEntity.ok(
            ApiResponse.success("Geofence deleted", null));
    }

    // GET /api/v1/geofences?ownerId=1
    @GetMapping
    public ResponseEntity<ApiResponse<List<GeofenceResponse>>> getByOwner(
            @RequestParam Long ownerId) {
        return ResponseEntity.ok(
            ApiResponse.success(geofenceService.getByOwner(ownerId)));
    }

    // GET /api/v1/geofences/vehicle/{vehicleId}
    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<ApiResponse<List<GeofenceResponse>>> getByVehicle(
            @PathVariable Long vehicleId) {
        return ResponseEntity.ok(
            ApiResponse.success(geofenceService.getByVehicle(vehicleId)));
    }
}
