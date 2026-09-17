package com.vibran.domain.geofence.service;

import com.vibran.domain.geofence.dto.request.CreateGeofenceRequest;
import com.vibran.domain.geofence.dto.request.UpdateGeofenceRequest;
import com.vibran.domain.geofence.dto.response.GeofenceResponse;
import com.vibran.domain.geofence.entity.Geofence;
import com.vibran.domain.geofence.mapper.GeofenceMapper;
import com.vibran.domain.geofence.repository.GeofenceRepository;
import com.vibran.domain.user.entity.User;
import com.vibran.domain.user.repositiory.UserRepository;
import com.vibran.domain.vehicle.entity.Vehicle;
import com.vibran.domain.vehicle.repository.VehicleRepository;
import com.vibran.shared.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service @RequiredArgsConstructor @Slf4j
@Transactional(readOnly = true)
public class GeofenceServiceImpl implements GeofenceService {

    private final GeofenceRepository geofenceRepository;
    private final UserRepository     userRepository;
    private final VehicleRepository  vehicleRepository;
    private final GeofenceMapper     mapper;

    @Override @Transactional
    public GeofenceResponse create(CreateGeofenceRequest request) {

        User owner = userRepository
            .findByIdAndIsDeletedFalse(request.getOwnerId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Owner user", request.getOwnerId()));

        // Guard: duplicate name per owner
        if (geofenceRepository.existsByOwnerIdAndNameAndIsActiveTrue(
                owner.getId(), request.getName()))
            throw new DuplicateResourceException(
                "Geofence '" + request.getName() +
                "' already exists for this owner");

        Geofence geofence = mapper.toEntity(request);
        geofence.setOwner(owner);

        // Optional: scope to a specific vehicle
        if (request.getVehicleId() != null) {
            Vehicle vehicle = vehicleRepository
                .findByIdAndIsDeletedFalse(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Vehicle", request.getVehicleId()));

            // Guard: vehicle must belong to owner
            if (!vehicle.getOwner().getId().equals(owner.getId()))
                throw new BusinessRuleException(
                    "Vehicle does not belong to this owner");

            geofence.setVehicle(vehicle);
        }

        Geofence saved = geofenceRepository.save(geofence);
        log.info("Geofence created: id={}, name={}, owner={}",
            saved.getId(), saved.getName(), owner.getEmail());

        return mapper.toResponse(saved);
    }

    @Override
    public GeofenceResponse getById(Long id) {
        return mapper.toResponse(findById(id));
    }

    @Override @Transactional
    public GeofenceResponse update(Long id, UpdateGeofenceRequest request) {
        Geofence g = findById(id);
        mapper.updateFromRequest(request, g);
        return mapper.toResponse(geofenceRepository.save(g));
    }

    @Override @Transactional
    public void deactivate(Long id) {
        Geofence g = findById(id);
        g.setIsActive(false);
        geofenceRepository.save(g);
        log.info("Geofence deactivated: id={}", id);
    }

    @Override @Transactional
    public void delete(Long id) {
        geofenceRepository.delete(findById(id));
        log.info("Geofence deleted: id={}", id);
    }

    @Override
    public List<GeofenceResponse> getByOwner(Long ownerId) {
        return geofenceRepository
            .findByOwnerIdAndIsActiveTrue(ownerId)
            .stream().map(mapper::toResponse).toList();
    }

    @Override
    public List<GeofenceResponse> getByVehicle(Long vehicleId) {
        return geofenceRepository
            .findByVehicleIdAndIsActiveTrue(vehicleId)
            .stream().map(mapper::toResponse).toList();
    }

    private Geofence findById(Long id) {
        return geofenceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Geofence", id));
    }
}
