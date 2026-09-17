package com.vibran.domain.vehicle.controller;

import com.vibran.domain.vehicle.dto.request.*;
import com.vibran.domain.vehicle.dto.response.*;
import com.vibran.domain.vehicle.service.interfaces.VehicleService;
import com.vibran.shared.response.ApiResponse;
import com.vibran.shared.response.PagedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    // POST /api/v1/vehicles
    @PostMapping
    public ResponseEntity<ApiResponse<VehicleResponse>> register(
            @Valid @RequestBody RegisterVehicleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Vehicle registered",
                        vehicleService.register(request)));
    }

    // GET /api/v1/vehicles/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleResponse>> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(vehicleService.getById(id)));
    }

    // PUT /api/v1/vehicles/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateVehicleRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Vehicle updated",
                        vehicleService.update(id, request)));
    }

    // DELETE /api/v1/vehicles/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        vehicleService.softDelete(id);
        return ResponseEntity.ok(
                ApiResponse.success("Vehicle deleted", null));
    }

    // GET /api/v1/vehicles?ownerId=1&page=0&size=20
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAll(
            @RequestParam(required = false) Long ownerId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        if (ownerId != null) {
            List<VehicleResponse> list = vehicleService.getByOwner(ownerId);
            return ResponseEntity.ok(ApiResponse.success(list));
        }
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("createdAt").descending());
        PagedResponse<VehicleResponse> paged = vehicleService.getAll(pageable);
        return ResponseEntity.ok(ApiResponse.success(paged));
    }

    // GET /api/v1/vehicles/matatus
    @GetMapping("/matatus")
    public ResponseEntity<ApiResponse<List<VehicleSummaryResponse>>> getMatatus() {
        return ResponseEntity.ok(
                ApiResponse.success(vehicleService.getMatatus()));
    }
}