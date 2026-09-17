package com.vibran.domain.energy.service.impl;


import com.vibran.config.WebSocketChannels;
import com.vibran.domain.energy.dto.response.EnergyLogResponse;
import com.vibran.domain.energy.dto.response.LiveEnergyResponse;
import com.vibran.domain.energy.entity.EnergyLog;
import com.vibran.domain.energy.mapper.EnergyMapper;
import com.vibran.domain.energy.repository.EnergyLogRepository;
import com.vibran.domain.energy.service.interfaces.EnergyService;
import com.vibran.domain.vehicle.entity.Vehicle;
import com.vibran.domain.vehicle.repository.VehicleRepository;
import com.vibran.shared.enums.EnergyType;
import com.vibran.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service @RequiredArgsConstructor
@Transactional(readOnly = true)
    public class EnergyServiceImpl implements EnergyService {

    private final EnergyLogRepository energyLogRepository;
    private final VehicleRepository   vehicleRepository;
    private final EnergyMapper        mapper;
    private final SimpMessagingTemplate messaging;

    @Override
    public LiveEnergyResponse getLiveLevel(Long vehicleId) {
        EnergyLog latest = energyLogRepository
                .findTopByVehicleIdOrderByRecordedAtDesc(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No energy data for vehicle id: " + vehicleId));

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vehicle", vehicleId));

        boolean isEv = vehicle.isElectric();
        return LiveEnergyResponse.builder()
                .vehicleId(vehicleId)
                .energyType(isEv ? EnergyType.ELECTRIC_CHARGE : EnergyType.FUEL)
                .levelPercent(latest.getLevelPercent() != null
                        ? latest.getLevelPercent().doubleValue() : null)
                .label(isEv ? "Battery" : "Fuel")
                .build();
    }

    @Override
    public List<EnergyLogResponse> getHistory(Long vehicleId,
                                              Instant from,
                                              Instant to) {

        return energyLogRepository
                .findByVehicleIdAndRecordedAtBetweenOrderByRecordedAtAsc(
                        vehicleId, from, to)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    /** Called by TelemetryService when ESP32 sends a reading */
    @Transactional
    public void recordAndPush(EnergyLog log) {
        EnergyLog saved = energyLogRepository.save(log);

        LiveEnergyResponse push = LiveEnergyResponse.builder()
                .vehicleId(saved.getVehicle().getId())
                .energyType(saved.getEnergyType())
                .levelPercent(saved.getLevelPercent() != null
                        ? saved.getLevelPercent().doubleValue() : null)
                .label(saved.getEnergyType() == EnergyType.ELECTRIC_CHARGE
                        ? "Battery" : "Fuel")
                .build();

        messaging.convertAndSend(
                WebSocketChannels.vehicleFuel(saved.getVehicle().getId()), push);
    }
}