package com.vibran.domain.sacco.controller;

import com.vibran.domain.sacco.dto.request.*;
import com.vibran.domain.sacco.dto.response.*;
import com.vibran.domain.sacco.service.interfaces.SaccoService;
import com.vibran.shared.response.ApiResponse;
import com.vibran.shared.response.PagedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/saccos")
@RequiredArgsConstructor
public class SaccoController {

    private final SaccoService saccoService;

    // POST /api/v1/saccos
    @PostMapping
    public ResponseEntity<ApiResponse<SaccoResponse>> create(
            @Valid @RequestBody CreateSaccoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("SACCO created",
                        saccoService.create(request)));
    }

    // GET /api/v1/saccos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SaccoResponse>> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(saccoService.getById(id)));
    }

    // GET /api/v1/saccos?page=0&size=20&county=Nairobi
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<SaccoResponse>>> getAll(
            @RequestParam(required = false) String county,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable p = PageRequest.of(page, size, Sort.by("name").ascending());
        PagedResponse<SaccoResponse> result = county != null
                ? saccoService.getByCounty(county, p)
                : saccoService.getAll(p);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // POST /api/v1/saccos/{id}/join
    @PostMapping("/{id}/join")
    public ResponseEntity<ApiResponse<SaccoMembershipResponse>> join(
            @PathVariable Long id,
            @Valid @RequestBody JoinSaccoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Joined SACCO",
                        saccoService.joinSacco(id, request)));
    }

    // POST /api/v1/saccos/leave?vehicleId=5
    @PostMapping("/leave")
    public ResponseEntity<ApiResponse<Void>> leave(
            @RequestParam Long vehicleId) {
        saccoService.leaveSacco(vehicleId);
        return ResponseEntity.ok(
                ApiResponse.success("Left SACCO", null));
    }

    // GET /api/v1/saccos/{id}/fleet
    // SACCO dashboard — all vehicles in this SACCO
    @GetMapping("/{id}/fleet")
    public ResponseEntity<ApiResponse<List<SaccoMembershipResponse>>> getFleet(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(saccoService.getSaccoFleet(id)));
    }
}