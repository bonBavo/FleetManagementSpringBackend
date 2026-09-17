package com.vibran.telemetry.document;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the GpsTelemetry MongoDB document.
 * Verifies that the entity and its nested components are correctly structured
 * and follow GeoJSON standards for geospatial queries.
 */
class GpsTelemetryTest {

    @Test
    @DisplayName("Should correctly create GpsTelemetry with nested GeoJsonPoint")
    void shouldCreateGpsTelemetryWithGeoJsonPoint() {
        // Given
        Instant now = Instant.now();
        GpsTelemetry.GeoJsonPoint location = GpsTelemetry.GeoJsonPoint.of(36.8219, -1.2921); // Nairobi

        // When
        GpsTelemetry telemetry = GpsTelemetry.builder()
                .id("test-id")
                .vehicleId(101L)
                .deviceSerial("SN-12345")
                .location(location)
                .speed(45.5)
                .heading(90)
                .timestamp(now)
                .ignition(true)
                .build();

        // Then
        assertNotNull(telemetry);
        assertEquals("test-id", telemetry.getId());
        assertEquals(101L, telemetry.getVehicleId());
        assertEquals("SN-12345", telemetry.getDeviceSerial());
        assertEquals(45.5, telemetry.getSpeed());
        assertEquals(90, telemetry.getHeading());
        assertEquals(now, telemetry.getTimestamp());
        assertTrue(telemetry.getIgnition());

        // Verify GeoJSON structure
        assertNotNull(telemetry.getLocation());
        assertEquals("Point", telemetry.getLocation().getType());
        assertEquals(2, telemetry.getLocation().getCoordinates().size());
        assertEquals(36.8219, telemetry.getLocation().getLongitude());
        assertEquals(-1.2921, telemetry.getLocation().getLatitude());
    }

    @Test
    @DisplayName("GeoJsonPoint should maintain [longitude, latitude] order")
    void geoJsonPointShouldMaintainCorrectOrder() {
        // Given
        double longitude = 36.8219;
        double latitude = -1.2921;

        // When
        GpsTelemetry.GeoJsonPoint point = GpsTelemetry.GeoJsonPoint.of(longitude, latitude);

        // Then
        // GeoJSON standard: index 0 is longitude, index 1 is latitude
        assertEquals(longitude, point.getCoordinates().get(0), "Index 0 must be Longitude");
        assertEquals(latitude, point.getCoordinates().get(1), "Index 1 must be Latitude");
    }

    @Test
    @DisplayName("Should build GpsTelemetry using all fields")
    void shouldBuildWithAllFields() {
        GpsTelemetry telemetry = GpsTelemetry.builder()
                .tripId(500L)
                .altitude(1600)
                .fuelLevel(75.0)
                .batteryVoltage(12.6)
                .gpsAccuracy(4.5)
                .satelliteCount(12)
                .sequenceId(1L)
                .build();

        assertEquals(500L, telemetry.getTripId());
        assertEquals(1600, telemetry.getAltitude());
        assertEquals(75.0, telemetry.getFuelLevel());
        assertEquals(12.6, telemetry.getBatteryVoltage());
        assertEquals(4.5, telemetry.getGpsAccuracy());
        assertEquals(12, telemetry.getSatelliteCount());
        assertEquals(1L, telemetry.getSequenceId());
    }
}
