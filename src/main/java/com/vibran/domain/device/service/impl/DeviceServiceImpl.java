package com.vibran.domain.device.service.impl;

import com.vibran.domain.device.dto.request.*;
import com.vibran.domain.device.dto.response.DeviceResponse;
import com.vibran.domain.device.entity.DeviceRegistry;
import com.vibran.domain.device.mapper.DeviceMapper;
import com.vibran.domain.device.repository.DeviceRegistryRepository;
import com.vibran.domain.device.service.interfaces.DeviceService;
import com.vibran.domain.vehicle.entity.Vehicle;
import com.vibran.domain.vehicle.repository.VehicleRepository;
import com.vibran.shared.enums.DeviceStatus;
import com.vibran.shared.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service @RequiredArgsConstructor @Slf4j
@Transactional(readOnly = true)
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRegistryRepository deviceRepository;
    private final VehicleRepository        vehicleRepository;
    private final DeviceMapper             mapper;

    @Override @Transactional
    public DeviceResponse register(RegisterDeviceRequest request) {
        if (deviceRepository.findByDeviceSerial(request.getDeviceSerial()).isPresent())
            throw new DuplicateResourceException(
                    "Device serial already registered: " + request.getDeviceSerial());

        return mapper.toResponse(
                deviceRepository.save(mapper.toEntity(request)));
    }

    @Override @Transactional
    public DeviceResponse assign(Long deviceId, AssignDeviceRequest request) {
        DeviceRegistry device = findById(deviceId);
        Vehicle vehicle = vehicleRepository
                .findByIdAndIsDeletedFalse(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vehicle", request.getVehicleId()));

        // Enforce one active device per vehicle — in service layer
        if (deviceRepository.existsByVehicleIdAndStatus(
                vehicle.getId(), DeviceStatus.ACTIVE))
            throw new BusinessRuleException(
                    "Vehicle " + vehicle.getPlateNumber() +
                            " already has an active device. Deactivate it first.");

        device.setVehicle(vehicle);
        device.setStatus(DeviceStatus.ACTIVE);
        device.setAssignedAt(Instant.now());

        log.info("Device {} assigned to vehicle {}",
                device.getDeviceSerial(), vehicle.getPlateNumber());
        return mapper.toResponse(deviceRepository.save(device));
    }

    @Override @Transactional
    public DeviceResponse deactivate(Long deviceId) {
        DeviceRegistry device = findById(deviceId);
        device.setStatus(DeviceStatus.INACTIVE);
        device.setDeactivatedAt(Instant.now());
        return mapper.toResponse(deviceRepository.save(device));
    }

    @Override
    public DeviceResponse getById(Long id) {
        return mapper.toResponse(findById(id));
    }

    @Override
    public List<DeviceResponse> getOfflineDevices(int thresholdMinutes) {
        var thresholdSeconds = thresholdMinutes * 60L;
        Instant threshold = Instant.now().minusSeconds(thresholdSeconds);
        return deviceRepository
                .findByStatusAndLastSeenAtBefore(DeviceStatus.ACTIVE, threshold)
                .stream().map(mapper::toResponse).toList();
    }

    private DeviceRegistry findById(Long id) {
        return deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device", id));
    }
}
