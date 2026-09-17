package com.vibran.telemetry.service.impl;

import com.vibran.config.WebSocketChannels;
import com.vibran.domain.alert.service.AlertRulesEngine;
import com.vibran.domain.alert.service.interfaces.AlertService;
import com.vibran.domain.energy.entity.EnergyLog;
import com.vibran.domain.energy.service.impl.EnergyServiceImpl;
import com.vibran.domain.vehicle.entity.Vehicle;
import com.vibran.domain.vehicle.repository.VehicleRepository;
import com.vibran.mqtt.TelemetryPayload;
import com.vibran.shared.enums.EnergyEventType;
import com.vibran.shared.enums.EnergyType;
import com.vibran.shared.exception.ResourceNotFoundException;
import com.vibran.telemetry.document.GpsTelemetry;
import com.vibran.telemetry.dto.response.LiveLocationResponse;
import com.vibran.telemetry.dto.response.RoutePointResponse;
import com.vibran.telemetry.dto.response.TripRouteResponse;
import com.vibran.telemetry.repository.GpsTelemetryRepository;
import com.vibran.telemetry.service.TelemetryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelemetryServiceImpl implements TelemetryService {

    private final GpsTelemetryRepository telemetryRepository;
    private final VehicleRepository      vehicleRepository;
    private final AlertRulesEngine       rulesEngine;
    private final AlertService           alertService;
    private final EnergyServiceImpl      energyService;
    private final SimpMessagingTemplate  messaging;

    // ── Main processing pipeline ──────────────────────────────
    // Called by MqttMessageHandler for every authenticated packet
    @Override
    public void process(TelemetryPayload payload, Long vehicleId,
                        Long tripId,             String deviceSerial) {

        // 1. Duplicate detection — reject redelivered packets
        if (payload.getSequenceId() != null &&
                telemetryRepository.existsByVehicleIdAndSequenceId(
                        vehicleId, payload.getSequenceId())) {
            log.debug("Duplicate packet rejected: vehicleId={}, seq={}",
                    vehicleId, payload.getSequenceId());
            return;
        }

        // 2. Build and save telemetry document to MongoDB
        GpsTelemetry doc = buildDocument(payload, vehicleId,
                tripId, deviceSerial);
        telemetryRepository.save(doc);

        // 3. Build live location response for WebSocket push
        LiveLocationResponse live = buildLiveResponse(payload, vehicleId);

        // 4. Push live position to Flutter map
        messaging.convertAndSend(
                WebSocketChannels.vehicleLocation(vehicleId), live);

        // 5. Save energy reading and push fuel/battery gauge update
        if (payload.getFuelLevel() != null) {
            Vehicle vehicle = vehicleRepository.findById(vehicleId)
                    .orElse(null);
            if (vehicle != null) {
                EnergyLog energyLog = buildEnergyLog(payload, vehicle, tripId);
                energyService.recordAndPush(energyLog);
            }
        }

        log.debug("Telemetry processed: vehicleId={}, speed={}",
                vehicleId, payload.getSpeed());
    }

    // ── Latest position ───────────────────────────────────────
    @Override
    public LiveLocationResponse getLatestPosition(Long vehicleId) {
        GpsTelemetry doc = telemetryRepository
                .findTopByVehicleIdOrderByTimestampDesc(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No telemetry data for vehicle id: " + vehicleId));

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vehicle", vehicleId));

        return LiveLocationResponse.builder()
                .vehicleId(vehicleId)
                .plateNumber(vehicle.getPlateNumber())
                .latitude(doc.getLocation().getLatitude())
                .longitude(doc.getLocation().getLongitude())
                .speed(doc.getSpeed())
                .heading(doc.getHeading())
                .ignition(doc.getIgnition())
                .fuelLevel(doc.getFuelLevel())
                .timestamp(doc.getTimestamp())
                .build();
    }

    // ── Trip route replay ─────────────────────────────────────
    @Override
    public TripRouteResponse getTripRoute(Long tripId) {
        List<GpsTelemetry> points = telemetryRepository
                .findByTripIdOrderByTimestampAsc(tripId);

        if (points.isEmpty())
            throw new ResourceNotFoundException(
                    "No telemetry data for trip id: " + tripId);

        List<RoutePointResponse> route = points.stream()
                .map(p -> RoutePointResponse.builder()
                        .latitude(p.getLocation().getLatitude())
                        .longitude(p.getLocation().getLongitude())
                        .speed(p.getSpeed())
                        .timestamp(p.getTimestamp())
                        .build())
                .toList();

        return TripRouteResponse.builder()
                .tripId(tripId)
                .vehicleId(points.get(0).getVehicleId())
                .pointCount(route.size())
                .points(route)
                .build();
    }

    // ── Vehicle history ───────────────────────────────────────
    @Override
    public List<RoutePointResponse> getVehicleHistory(Long vehicleId,
                                                      String from,
                                                      String to) {
        Instant start = Instant.parse(from);
        Instant end   = Instant.parse(to);

        return telemetryRepository
                .findByVehicleIdAndTimestampBetweenOrderByTimestampAsc(
                        vehicleId, start, end)
                .stream()
                .map(p -> RoutePointResponse.builder()
                        .latitude(p.getLocation().getLatitude())
                        .longitude(p.getLocation().getLongitude())
                        .speed(p.getSpeed())
                        .timestamp(p.getTimestamp())
                        .build())
                .toList();
    }

    // ── Private builders ──────────────────────────────────────
    private GpsTelemetry buildDocument(TelemetryPayload payload,
                                       Long vehicleId,
                                       Long tripId,
                                       String deviceSerial) {
        GpsTelemetry.GeoJsonPoint location = null;
        if (payload.getLatitude() != null && payload.getLongitude() != null) {
            location = GpsTelemetry.GeoJsonPoint.of(
                    payload.getLongitude(),   // GeoJSON: longitude first
                    payload.getLatitude());
        }

        return GpsTelemetry.builder()
                .vehicleId(vehicleId)
                .tripId(tripId)
                .deviceSerial(deviceSerial)
                .location(location)
                .speed(payload.getSpeed())
                .heading(payload.getHeading())
                .altitude(payload.getAltitude())
                .ignition(payload.getIgnition())
                .fuelLevel(payload.getFuelLevel())
                .batteryVoltage(payload.getBatteryVoltage())
                .gpsAccuracy(payload.getGpsAccuracy())
                .sequenceId(payload.getSequenceId())
                .timestamp(payload.getTimestamp() != null
                        ? Instant.ofEpochMilli(payload.getTimestamp())
                        : Instant.now())
                .build();
    }

    private LiveLocationResponse buildLiveResponse(TelemetryPayload payload,
                                                   Long vehicleId) {
        return LiveLocationResponse.builder()
                .vehicleId(vehicleId)
                .latitude(payload.getLatitude())
                .longitude(payload.getLongitude())
                .speed(payload.getSpeed())
                .heading(payload.getHeading())
                .ignition(payload.getIgnition())
                .fuelLevel(payload.getFuelLevel())
                .timestamp(payload.getTimestamp() != null
                        ? Instant.ofEpochMilli(payload.getTimestamp())
                        : Instant.now())
                .build();
    }

    private EnergyLog buildEnergyLog(TelemetryPayload payload,
                                     Vehicle vehicle,
                                     Long tripId) {
        return EnergyLog.builder()
                .vehicle(vehicle)
                .fuelLevelPct(BigDecimal.valueOf(payload.getFuelLevel()))
                .energyType(vehicle.isElectric()
                        ? EnergyType.ELECTRIC_CHARGE : EnergyType.FUEL)
                .eventType(EnergyEventType.PERIODIC_SNAPSHOT)
                .latitude(payload.getLatitude() != null
                        ? BigDecimal.valueOf(payload.getLatitude()) : null)
                .longitude(payload.getLongitude() != null
                        ? BigDecimal.valueOf(payload.getLongitude()) : null)
                .build();
    }
}