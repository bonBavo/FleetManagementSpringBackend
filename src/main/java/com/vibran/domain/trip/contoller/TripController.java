package com.vibran.domain.trip.contoller;


import com.vibran.domain.trip.dto.request.*;
import com.vibran.domain.trip.dto.response.TripResponse;
import com.vibran.domain.trip.service.interfaces.TripService;
import com.vibran.shared.response.ApiResponse;
import com.vibran.shared.response.PagedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;

    // POST /api/v1/trips/start
    @PostMapping("/start")
    public ResponseEntity<ApiResponse<TripResponse>> start(
            @Valid @RequestBody StartTripRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Trip started",
                        tripService.start(request)));
    }

    // POST /api/v1/trips/{id}/end
    @PostMapping("/{id}/end")
    public ResponseEntity<ApiResponse<TripResponse>> end(
            @PathVariable Long id,
            @RequestBody EndTripRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Trip ended",
                        tripService.end(id, request)));
    }

    // GET /api/v1/trips/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TripResponse>> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(tripService.getById(id)));
    }

    // GET /api/v1/trips/active?vehicleId=5
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<TripResponse>> getActiveTrip(
            @RequestParam Long vehicleId) {
        return ResponseEntity.ok(
                ApiResponse.success(tripService.getActiveTrip(vehicleId)));
    }

    // GET /api/v1/trips/vehicle/{vehicleId}?page=0&size=20
    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<ApiResponse<PagedResponse<TripResponse>>> getVehicleHistory(
            @PathVariable Long vehicleId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable p = PageRequest.of(page, size,
                Sort.by("startTime").descending());
        return ResponseEntity.ok(
                ApiResponse.success(
                        tripService.getVehicleHistory(vehicleId, p)));
    }

    // GET /api/v1/trips/driver/{driverId}?page=0&size=20
    @GetMapping("/driver/{driverId}")
    public ResponseEntity<ApiResponse<PagedResponse<TripResponse>>> getDriverHistory(
            @PathVariable Long driverId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable p = PageRequest.of(page, size,
                Sort.by("startTime").descending());
        return ResponseEntity.ok(
                ApiResponse.success(
                        tripService.getDriverHistory(driverId, p)));
    }
}
