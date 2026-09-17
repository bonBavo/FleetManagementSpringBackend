package com.vibran.domain.alert.service;

// ── AlertRulesEngine.java ─────────────────────────────────────
// Called by TelemetryService on every incoming MQTT packet.
// Each rule is a separate method — easy to add more rules later.


import com.vibran.domain.alert.entity.Alert;
import com.vibran.domain.alert.repository.AlertRepository;
import com.vibran.domain.device.entity.DeviceRegistry;
import com.vibran.domain.geofence.repository.GeofenceRepository;
import com.vibran.domain.trip.entity.Trip;
import com.vibran.domain.vehicle.entity.Vehicle;
import com.vibran.shared.enums.AlertSeverity;
import com.vibran.shared.enums.AlertType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Pure rules evaluation — no persistence here.
 * Returns a list of Alert objects to be saved by AlertService.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AlertRulesEngine {

    private final AlertRepository    alertRepository;
    private final GeofenceRepository geofenceRepository;

    // Speed limit — configurable per vehicle category later
    private static final double SPEED_LIMIT_KMH        = 80.0;
    private static final double FUEL_LOW_THRESHOLD_PCT  = 15.0;
    private static final double FUEL_THEFT_DROP_PCT     = 10.0; // sudden drop
    private static final double BATTERY_LOW_THRESHOLD   = 20.0;
    private static final int    COOLDOWN_MINUTES         = 5;

    /**
     * Evaluate all rules for one telemetry packet.
     * @param vehicle    the vehicle this packet belongs to
     * @param device     ESP32 that sent the packet
     * @param trip       current active trip (can be null)
     * @param speed      current speed km/h
     * @param latitude   current latitude
     * @param longitude  current longitude
     * @param levelPct   fuel % for ICE, battery % for EV
     * @param prevLevel  previous reading for sudden-drop detection
     * @param ignition   ignition state
     */
    public List<Alert> evaluate(
            Vehicle       vehicle,
            DeviceRegistry device,
            Trip          trip,
            Double        speed,
            BigDecimal    latitude,
            BigDecimal    longitude,
            Double        levelPct,
            Double        prevLevel,
            Boolean       ignition) {

        List<Alert> triggered = new ArrayList<>();

        if (speed != null)     checkOverspeed(vehicle, device, trip, speed, latitude, longitude, triggered);
        if (levelPct != null)  checkEnergyLevel(vehicle, device, trip, levelPct, prevLevel, latitude, longitude, triggered);
        if (latitude != null)  checkGeofence(vehicle, device, trip, latitude, longitude, triggered);

        return triggered;
    }

    // ── Rule 1: Overspeed ─────────────────────────────────────
    private void checkOverspeed(Vehicle v, DeviceRegistry d, Trip trip,
                                double speed, BigDecimal lat, BigDecimal lon,
                                List<Alert> out) {
        if (speed <= SPEED_LIMIT_KMH) return;
        if (isThrottled(v.getId(), AlertType.OVERSPEED)) return;

        out.add(Alert.builder()
                .vehicle(v).device(d).trip(trip)
                .alertType(AlertType.OVERSPEED)
                .severity(speed > 120 ? AlertSeverity.CRITICAL : AlertSeverity.HIGH)
                .title("Overspeed detected — " + v.getPlateNumber())
                .message(String.format("Vehicle travelling at %.1f km/h (limit: %.0f km/h)",
                        speed, SPEED_LIMIT_KMH))
                .speedAtAlert(BigDecimal.valueOf(speed))
                .thresholdValue(BigDecimal.valueOf(SPEED_LIMIT_KMH))
                .actualValue(BigDecimal.valueOf(speed))
                .latitude(lat).longitude(lon)
                .cooldownUntil(Instant.now().plusSeconds((60*COOLDOWN_MINUTES)))
                .build());
    }

    // ── Rule 2: Low fuel / Low battery / Fuel theft ───────────
    private void checkEnergyLevel(Vehicle v, DeviceRegistry d, Trip trip,
                                  double level, Double prev,
                                  BigDecimal lat, BigDecimal lon,
                                  List<Alert> out) {
        boolean isEv = v.isElectric();

        // Low energy warning
        double threshold = isEv ? BATTERY_LOW_THRESHOLD : FUEL_LOW_THRESHOLD_PCT;
        AlertType lowType = isEv ? AlertType.LOW_BATTERY : AlertType.LOW_FUEL;

        if (level < threshold && !isThrottled(v.getId(), lowType)) {
            out.add(Alert.builder()
                    .vehicle(v).device(d).trip(trip)
                    .alertType(lowType)
                    .severity(level < 5 ? AlertSeverity.CRITICAL : AlertSeverity.HIGH)
                    .title((isEv ? "Low battery" : "Low fuel") + " — " + v.getPlateNumber())
                    .message(String.format("%s at %.1f%%",
                            isEv ? "Battery" : "Fuel tank", level))
                    .actualValue(BigDecimal.valueOf(level))
                    .thresholdValue(BigDecimal.valueOf(threshold))
                    .latitude(lat).longitude(lon)
                    .cooldownUntil(Instant.now().plusSeconds((60*30)))
                    .build());
        }

        // Fuel theft — sudden drop (ICE only)
        if (!isEv && prev != null && (prev - level) > FUEL_THEFT_DROP_PCT
                && !isThrottled(v.getId(), AlertType.FUEL_THEFT)) {
            out.add(Alert.builder()
                    .vehicle(v).device(d).trip(trip)
                    .alertType(AlertType.FUEL_THEFT)
                    .severity(AlertSeverity.CRITICAL)
                    .title("Fuel theft suspected — " + v.getPlateNumber())
                    .message(String.format("Fuel dropped %.1f%% → %.1f%% suddenly",
                            prev, level))
                    .actualValue(BigDecimal.valueOf(prev - level))
                    .thresholdValue(BigDecimal.valueOf(FUEL_THEFT_DROP_PCT))
                    .latitude(lat).longitude(lon)
                    .cooldownUntil(Instant.now().plusSeconds((60*30)))
                    .build());
        }
    }

    // ── Rule 3: Geofence exit ─────────────────────────────────
    private void checkGeofence(Vehicle v, DeviceRegistry d, Trip trip,
                               BigDecimal lat, BigDecimal lon,
                               List<Alert> out) {

        // Fetch active geofences for this vehicle's owner
        geofenceRepository
                .findActiveGeofencesForVehicle(v.getOwner().getId(), v.getId())
                .forEach(fence -> {
                    double dist = haversineDistanceM(
                            lat.doubleValue(), lon.doubleValue(),
                            fence.getCenterLatitude().doubleValue(),
                            fence.getCenterLongitude().doubleValue());

                    boolean outside = dist > fence.getRadiusMeters();

                    if (outside && fence.getAlertOnExit()
                            && !isThrottled(v.getId(), AlertType.GEOFENCE_EXIT)) {

                        out.add(Alert.builder()
                                .vehicle(v).device(d).trip(trip)
                                .alertType(AlertType.GEOFENCE_EXIT)
                                .severity(AlertSeverity.HIGH)
                                .title("Geofence exit — " + v.getPlateNumber())
                                .message("Vehicle left zone: " + fence.getName())
                                .latitude(lat).longitude(lon)
                                .cooldownUntil(Instant.now().plusSeconds(COOLDOWN_MINUTES * 60L))
                                .build());
                    }
                });
    }

    // ── Throttle check ────────────────────────────────────────
    private boolean isThrottled(Long vehicleId, AlertType type) {
        return alertRepository
                .existsByVehicleIdAndAlertTypeAndCooldownUntilAfter(
                        vehicleId, type, Instant.now());
    }

    // ── Haversine distance formula ────────────────────────────
    private double haversineDistanceM(double lat1, double lon1,
                                      double lat2, double lon2) {
        final int R = 6371000; // Earth radius metres
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
