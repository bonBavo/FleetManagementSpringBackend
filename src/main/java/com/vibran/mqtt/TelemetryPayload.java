package com.vibran.mqtt;

import lombok.Data;

@Data
public class TelemetryPayload {
    private String  deviceToken;    // identifies which ESP32
    private Double  latitude;
    private Double  longitude;
    private Double  speed;          // km/h
    private Integer heading;        // degrees 0-360
    private Integer altitude;       // metres
    private Boolean ignition;
    private Double  fuelLevel;      // 0-100 % (fuel or battery)
    private Double  batteryVoltage; // volts
    private Double  gpsAccuracy;    // metres
    private Long    sequenceId;     // duplicate detection
    private Long    timestamp;      // epoch millis from ESP32
}
