package com.vibran.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vibran.domain.alert.entity.Alert;
import com.vibran.domain.alert.service.AlertRulesEngine;
import com.vibran.domain.alert.service.interfaces.AlertService;
import com.vibran.domain.device.entity.DeviceRegistry;
import com.vibran.domain.device.repository.DeviceRegistryRepository;
import com.vibran.domain.energy.entity.EnergyLog;
import com.vibran.domain.energy.service.impl.EnergyServiceImpl;
import com.vibran.domain.trip.entity.Trip;
import com.vibran.domain.trip.repository.TripRepository;
import com.vibran.domain.vehicle.entity.Vehicle;
import com.vibran.shared.enums.DeviceStatus;
import com.vibran.shared.enums.EnergyEventType;
import com.vibran.shared.enums.EnergyType;
import com.vibran.shared.enums.TripStatus;
import com.vibran.telemetry.service.TelemetryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class MqttMessageHandler {

    private final DeviceRegistryRepository deviceRepository;
    private final TripRepository tripRepository;
    private final AlertRulesEngine rulesEngine;
    private final AlertService alertService;
    private final EnergyServiceImpl energyService;
    private final ObjectMapper objectMapper;
    private final TelemetryService telemetryService;

    /**
     * Entry point — every MQTT message from any ESP32 comes here.
     * Wired to mqttInputChannel via @ServiceActivator.
     */
    @ServiceActivator(inputChannel = "mqttInputChannel")
    @Transactional
    public void handleMessage(Message<String> message) {
        try {
            TelemetryPayload payload = objectMapper.readValue(
                    message.getPayload(), TelemetryPayload.class);

            processTelemetry(payload);

        } catch (Exception e) {
            log.error("Failed to process MQTT message: {}", e.getMessage());
        }
    }

    private void processTelemetry(TelemetryPayload payload) {

        // 1. Authenticate device
        Optional<DeviceRegistry> deviceOpt =
                deviceRepository.findByDeviceToken(payload.getDeviceToken());

        if (deviceOpt.isEmpty()) {
            log.warn("Rejected: unknown device token");
            return;
        }

        DeviceRegistry device = deviceOpt.get();

        if (device.getStatus() != DeviceStatus.ACTIVE) {
            log.warn("Rejected: device {} not active", device.getDeviceSerial());
            return;
        }

        // 2. Validate GPS accuracy
        if (payload.getGpsAccuracy() != null && payload.getGpsAccuracy() > 50) {
            log.debug("Rejected: GPS accuracy too low ({}m)",
                    payload.getGpsAccuracy());
            return;
        }

        Vehicle vehicle = device.getVehicle();

        // 3. Update device heartbeat
        device.setLastSeenAt(Instant.now());
        deviceRepository.save(device);

        // 4. Get active trip
        Trip activeTrip = tripRepository
                .findByVehicleIdAndStatus(vehicle.getId(), TripStatus.ACTIVE)
                .orElse(null);

        Long tripId = activeTrip != null ? activeTrip.getId() : null;

        // 5. Delegate ALL processing to TelemetryService
        //    It handles: MongoDB save, WebSocket push,
        //                energy log, alert rules, notifications
        telemetryService.process(
                payload,
                vehicle.getId(),
                tripId,
                device.getDeviceSerial()
        );

        // 6. Run alert rules engine
        List<Alert> alerts = rulesEngine.evaluate(
                vehicle, device, activeTrip,
                payload.getSpeed(),
                payload.getLatitude()  != null
                        ? new BigDecimal(payload.getLatitude().toString())  : null,
                payload.getLongitude() != null
                        ? new BigDecimal(payload.getLongitude().toString()) : null,
                payload.getFuelLevel(),
                null,
                payload.getIgnition()
        );

        // 7. Save alerts and push to Flutter + FCM
        alertService.saveAndPush(alerts);
    }
}