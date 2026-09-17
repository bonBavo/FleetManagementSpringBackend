package com.vibran.telemetry.service;


import com.vibran.mqtt.TelemetryPayload;
import com.vibran.telemetry.dto.response.LiveLocationResponse;
import com.vibran.telemetry.dto.response.RoutePointResponse;
import com.vibran.telemetry.dto.response.TripRouteResponse;

import java.util.List;

public interface TelemetryService {

    // Called by MqttMessageHandler on every incoming packet
    void process(TelemetryPayload payload, Long vehicleId,
                 Long tripId, String deviceSerial);

    // Flutter: get latest position for one vehicle
    LiveLocationResponse getLatestPosition(Long vehicleId);

    // Flutter: trip replay — draw the full route on the map
    TripRouteResponse getTripRoute(Long tripId);

    // Flutter: route history for a vehicle between dates
    List<RoutePointResponse> getVehicleHistory(Long vehicleId,
                                               String from, String to);
}

