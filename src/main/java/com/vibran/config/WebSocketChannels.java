package com.vibran.config;
public final class WebSocketChannels {

    private WebSocketChannels() {}

    // ── Flutter SUBSCRIBES to these (server pushes) ───────────

    // Live GPS location update — every 3-5 seconds per vehicle
    // Example: /topic/vehicle/5/location
    public static final String VEHICLE_LOCATION = "/topic/vehicle/%d/location";

    // Alert fired for a vehicle — theft, overspeed, geofence
    // Example: /topic/vehicle/5/alerts
    public static final String VEHICLE_ALERTS = "/topic/vehicle/%d/alerts";

    // Fuel level update stream
    // Example: /topic/vehicle/5/fuel
    public static final String VEHICLE_FUEL = "/topic/vehicle/%d/fuel";

    // Trip started or ended event
    // Example: /topic/vehicle/5/trip
    public static final String VEHICLE_TRIP = "/topic/vehicle/%d/trip";

    // ── User-specific queue (private — only that user receives) ──

    // Push notification for a specific user
    // Example: /user/queue/notifications
    public static final String USER_NOTIFICATIONS = "/user/queue/notifications";

    // ── Helper: build topic string with vehicleId ─────────────
    public static String vehicleLocation(Long vehicleId) {
        return String.format(VEHICLE_LOCATION, vehicleId);
    }

    public static String vehicleAlerts(Long vehicleId) {
        return String.format(VEHICLE_ALERTS, vehicleId);
    }

    public static String vehicleFuel(Long vehicleId) {
        return String.format(VEHICLE_FUEL, vehicleId);
    }

    public static String vehicleTrip(Long vehicleId) {
        return String.format(VEHICLE_TRIP, vehicleId);
    }
}
