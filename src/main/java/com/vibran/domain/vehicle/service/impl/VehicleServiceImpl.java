package com.vibran.domain.vehicle.service.impl;

import com.vibran.domain.vehicle.dto.request.*;
import com.vibran.domain.vehicle.dto.response.*;
import com.vibran.domain.vehicle.entity.*;
import com.vibran.domain.vehicle.mapper.VehicleMapper;
import com.vibran.domain.vehicle.repository.*;
import com.vibran.domain.user.entity.User;
import com.vibran.domain.user.repositiory.UserRepository;
import com.vibran.domain.vehicle.service.interfaces.VehicleService;
import com.vibran.shared.enums.VehicleCategory;
import com.vibran.shared.exception.*;
import com.vibran.shared.response.PagedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service @RequiredArgsConstructor @Slf4j
@Transactional(readOnly = true)
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository      vehicleRepository;
    private final VehicleMakeRepository  makeRepository;
    private final VehicleModelRepository modelRepository;
    private final UserRepository         userRepository;
    private final VehicleMapper          mapper;

    @Override @Transactional
    public VehicleResponse register(RegisterVehicleRequest request) {

        if (vehicleRepository.existsByPlateNumber(request.getPlateNumber()))
            throw new DuplicateResourceException(
                    "Plate number already registered: " + request.getPlateNumber());

        if (request.getVin() != null &&
                vehicleRepository.existsByVin(request.getVin()))
            throw new DuplicateResourceException(
                    "VIN already registered: " + request.getVin());

        User owner = userRepository.findByIdAndIsDeletedFalse(request.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Owner user", request.getOwnerId()));

        VehicleModel model = modelRepository.findById(request.getModelId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "VehicleModel", request.getModelId()));

        Vehicle vehicle = mapper.toEntity(request);
        vehicle.setOwner(owner);
        vehicle.setModel(model);

        Vehicle saved = vehicleRepository.save(vehicle);
        log.info("Vehicle registered: id={}, plate={}", saved.getId(),
                saved.getPlateNumber());
        return mapper.toResponse(saved);
    }

    @Override
    public VehicleResponse getById(Long id) {
        return mapper.toResponse(
                vehicleRepository.findWithDetailsById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Vehicle", id)));
    }

    @Override @Transactional
    public VehicleResponse update(Long id, UpdateVehicleRequest request) {
        Vehicle v = findActiveById(id);
        mapper.updateFromRequest(request, v);
        return mapper.toResponse(vehicleRepository.save(v));
    }

    @Override @Transactional
    public void softDelete(Long id) {
        findActiveById(id);
        vehicleRepository.softDelete(id, Instant.now());
    }

    @Override
    public List<VehicleResponse> getByOwner(Long ownerId) {
        return vehicleRepository.findByOwnerIdAndIsDeletedFalse(ownerId)
                .stream().map(mapper::toResponse).toList();
    }

    @Override
    public List<VehicleSummaryResponse> getMatatus() {
        return vehicleRepository.findByVehicleCategoryInAndIsDeletedFalse(
                        List.of(VehicleCategory.MATATU_14, VehicleCategory.MATATU_33))
                .stream().map(mapper::toSummaryResponse).toList();
    }

    @Override
    public PagedResponse<VehicleResponse> getAll(Pageable pageable) {
        Page<Vehicle> page = vehicleRepository.findByIsDeletedFalse(pageable);
        return PagedResponse.<VehicleResponse>builder()
                .content(page.getContent().stream().map(mapper::toResponse).toList())
                .page(page.getNumber()).size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages()).last(page.isLast())
                .build();
    }

    private Vehicle findActiveById(Long id) {
        return vehicleRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", id));
    }
}