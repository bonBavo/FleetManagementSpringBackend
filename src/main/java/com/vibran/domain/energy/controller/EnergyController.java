package com.vibran.domain.energy.controller;


import com.vibran.domain.energy.dto.response.EnergyLogResponse;
import com.vibran.domain.energy.dto.response.LiveEnergyResponse;
import com.vibran.domain.energy.service.interfaces.EnergyService;
import com.vibran.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/energy")
@RequiredArgsConstructor
public class EnergyController {

    private final EnergyService energyService;

    // GET /api/v1/energy/{vehicleId}/live
    // Flutter dashboard gauge — fuel % or battery % live reading
    @GetMapping("/{vehicleId}/live")
    public ResponseEntity<ApiResponse<LiveEnergyResponse>> getLive(
            @PathVariable Long vehicleId) {
        return ResponseEntity.ok(
                ApiResponse.success(energyService.getLiveLevel(vehicleId)));
    }

    // GET /vehicles/1/history?from=2026-06-01T10:00:00Z&to=2026-06-01T12:00:00Z
    // Flutter history chart — fuel or charge level over time
    @GetMapping("/{vehicleId}/history")
    public ResponseEntity<ApiResponse<List<EnergyLogResponse>>> getHistory(
            @PathVariable Long vehicleId,
            @RequestParam Instant from,
            @RequestParam Instant to) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        energyService.getHistory(vehicleId, from, to)
                )
        );
    }
}
