package com.vibran.domain.alert.controller;

import com.vibran.domain.alert.dto.request.AcknowledgeAlertRequest;
import com.vibran.domain.alert.dto.request.ResolveAlertRequest;
import com.vibran.domain.alert.dto.response.AlertResponse;
import com.vibran.domain.alert.dto.response.AlertSummaryResponse;
import com.vibran.domain.alert.service.interfaces.AlertService;
import com.vibran.shared.response.ApiResponse;
import com.vibran.shared.response.PagedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    // GET /api/v1/alerts/vehicle/{vehicleId}/unacknowledged
    // Flutter: badge count + notification list
    @GetMapping("/vehicle/{vehicleId}/unacknowledged")
    public ResponseEntity<ApiResponse<List<AlertSummaryResponse>>> getUnacknowledged(
            @PathVariable Long vehicleId) {
        return ResponseEntity.ok(
                ApiResponse.success(alertService.getUnacknowledged(vehicleId)));
    }

    // GET /api/v1/alerts/vehicle/{vehicleId}/count
    // Flutter: notification badge number
    @GetMapping("/vehicle/{vehicleId}/count")
    public ResponseEntity<ApiResponse<Long>> getUnacknowledgedCount(
            @PathVariable Long vehicleId) {
        return ResponseEntity.ok(
                ApiResponse.success(alertService.countUnacknowledged(vehicleId)));
    }

    // GET /api/v1/alerts/vehicle/{vehicleId}?page=0&size=20
    // Flutter: full alert history screen
    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<ApiResponse<PagedResponse<AlertResponse>>> getHistory(
            @PathVariable Long vehicleId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable p = PageRequest.of(page, size,
                Sort.by("triggeredAt").descending());
        return ResponseEntity.ok(
                ApiResponse.success(alertService.getHistory(vehicleId, p)));
    }

    // GET /api/v1/alerts/owner/{ownerId}/unacknowledged
    // Flutter: fleet-wide alert summary for owner dashboard
    @GetMapping("/owner/{ownerId}/unacknowledged")
    public ResponseEntity<ApiResponse<List<AlertSummaryResponse>>> getOwnerAlerts(
            @PathVariable Long ownerId) {
        return ResponseEntity.ok(
                ApiResponse.success(alertService.getOwnerUnacknowledged(ownerId)));
    }

    // POST /api/v1/alerts/{id}/acknowledge
    @PostMapping("/{id}/acknowledge")
    public ResponseEntity<ApiResponse<AlertResponse>> acknowledge(
            @PathVariable Long id,
            @Valid @RequestBody AcknowledgeAlertRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Alert acknowledged",
                        alertService.acknowledge(id, request)));
    }

    // POST /api/v1/alerts/{id}/resolve
    @PostMapping("/{id}/resolve")
    public ResponseEntity<ApiResponse<AlertResponse>> resolve(
            @PathVariable Long id,
            @Valid @RequestBody ResolveAlertRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Alert resolved",
                        alertService.resolve(id, request)));
    }
}

