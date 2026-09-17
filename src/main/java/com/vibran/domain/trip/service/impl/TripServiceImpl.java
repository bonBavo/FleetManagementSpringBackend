package com.vibran.domain.trip.service.impl;


import com.vibran.config.WebSocketChannels;
import com.vibran.domain.device.entity.DeviceRegistry;
import com.vibran.domain.device.repository.DeviceRegistryRepository;
import com.vibran.domain.trip.dto.request.*;
import com.vibran.domain.trip.dto.response.TripResponse;
import com.vibran.domain.trip.entity.Trip;
import com.vibran.domain.trip.mapper.TripMapper;
import com.vibran.domain.trip.repository.TripRepository;
import com.vibran.domain.trip.service.interfaces.TripService;
import com.vibran.domain.user.entity.User;
import com.vibran.domain.user.repositiory.UserRepository;
import com.vibran.domain.vehicle.entity.Vehicle;
import com.vibran.domain.vehicle.repository.VehicleRepository;
import com.vibran.shared.enums.TripStatus;
import com.vibran.shared.exception.*;
import com.vibran.shared.response.PagedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service @RequiredArgsConstructor @Slf4j
@Transactional(readOnly = true)
public class TripServiceImpl implements TripService {

    private final TripRepository          tripRepository;
    private final VehicleRepository       vehicleRepository;
    private final DeviceRegistryRepository deviceRepository;
    private final UserRepository          userRepository;
    private final TripMapper              mapper;
    private final SimpMessagingTemplate   messaging;

    @Override @Transactional
    public TripResponse start(StartTripRequest request) {

        Vehicle vehicle = vehicleRepository
                .findByIdAndIsDeletedFalse(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vehicle", request.getVehicleId()));

        // Guard: no overlapping trips
        if (tripRepository.existsByVehicleIdAndStatus(
                vehicle.getId(), TripStatus.ACTIVE))
            throw new BusinessRuleException(
                    "Vehicle " + vehicle.getPlateNumber() +
                            " already has an active trip");

        DeviceRegistry device = deviceRepository
                .findById(request.getDeviceId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Device", request.getDeviceId()));

        Trip trip = mapper.toEntity(request);
        trip.setVehicle(vehicle);
        trip.setDevice(device);

        if (request.getDriverId() != null) {
            User driver = userRepository
                    .findByIdAndIsDeletedFalse(request.getDriverId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Driver user", request.getDriverId()));
            trip.setDriver(driver);
        }

        Trip saved = tripRepository.save(trip);

        // Notify Flutter via WebSocket
        messaging.convertAndSend(
                WebSocketChannels.vehicleTrip(vehicle.getId()),
                mapper.toResponse(saved));

        log.info("Trip started: id={}, vehicle={}",
                saved.getId(), vehicle.getPlateNumber());
        return mapper.toResponse(saved);
    }

    @Override @Transactional
    public TripResponse end(Long tripId, EndTripRequest request) {
        Trip trip = findById(tripId);

        if (trip.getStatus() != TripStatus.ACTIVE)
            throw new BusinessRuleException("Trip is not active");

        trip.setStatus(TripStatus.COMPLETED);
        trip.setEndTime(Instant.now());
        trip.setEndLatitude(request.getEndLatitude());
        trip.setEndLongitude(request.getEndLongitude());
        trip.setEndAddress(request.getEndAddress());
        trip.setDurationMinutes((int) ChronoUnit.MINUTES.between(
                trip.getStartTime(), trip.getEndTime()));

        // Matatu-specific
        if (request.getPassengerCount() != null)
            trip.setPassengerCount(request.getPassengerCount());
        if (request.getFareCollectedKes() != null)
            trip.setFareCollectedKes(request.getFareCollectedKes());

        Trip saved = tripRepository.save(trip);

        // Notify Flutter
        messaging.convertAndSend(
                WebSocketChannels.vehicleTrip(trip.getVehicle().getId()),
                mapper.toResponse(saved));

        return mapper.toResponse(saved);
    }

    @Override
    public TripResponse getById(Long id) {
        return mapper.toResponse(tripRepository.findWithDetailsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", id)));
    }

    @Override
    public TripResponse getActiveTrip(Long vehicleId) {
        return mapper.toResponse(
                tripRepository.findByVehicleIdAndStatus(vehicleId, TripStatus.ACTIVE)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "No active trip for vehicle id: " + vehicleId)));
    }

    @Override
    public PagedResponse<TripResponse> getVehicleHistory(Long vehicleId,
                                                         Pageable p) {
        Page<Trip> page = tripRepository
                .findByVehicleIdOrderByStartTimeDesc(vehicleId, p);
        return toPagedResponse(page);
    }

    @Override
    public PagedResponse<TripResponse> getDriverHistory(Long driverId,
                                                        Pageable p) {
        Page<Trip> page = tripRepository
                .findByDriverIdOrderByStartTimeDesc(driverId, p);
        return toPagedResponse(page);
    }

    private Trip findById(Long id) {
        return tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", id));
    }

    private PagedResponse<TripResponse> toPagedResponse(Page<Trip> page) {
        return PagedResponse.<TripResponse>builder()
                .content(page.getContent().stream()
                        .map(mapper::toResponse).toList())
                .page(page.getNumber()).size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages()).last(page.isLast())
                .build();
    }
}

