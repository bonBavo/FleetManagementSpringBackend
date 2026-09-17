package com.vibran.domain.device.contoller;


import com.vibran.domain.device.dto.request.*;
import com.vibran.domain.device.dto.response.DeviceResponse;
import com.vibran.domain.device.service.interfaces.DeviceService;
import com.vibran.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    // POST /api/v1/devices
    @PostMapping
    public ResponseEntity<ApiResponse<DeviceResponse>> register(
            @Valid @RequestBody RegisterDeviceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Device registered",
                        deviceService.register(request)));
    }

    // GET /api/v1/devices/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DeviceResponse>> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(deviceService.getById(id)));
    }

    // POST /api/v1/devices/{id}/assign
    @PostMapping("/{id}/assign")
    public ResponseEntity<ApiResponse<DeviceResponse>> assign(
            @PathVariable Long id,
            @Valid @RequestBody AssignDeviceRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Device assigned",
                        deviceService.assign(id, request)));
    }

    // POST /api/v1/devices/{id}/deactivate
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<DeviceResponse>> deactivate(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Device deactivated",
                        deviceService.deactivate(id)));
    }

    // GET /api/v1/devices/offline?thresholdMinutes=10
    @GetMapping("/offline")
    public ResponseEntity<ApiResponse<List<DeviceResponse>>> getOffline(
            @RequestParam(defaultValue = "10") int thresholdMinutes) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        deviceService.getOfflineDevices(thresholdMinutes)));
    }
}
