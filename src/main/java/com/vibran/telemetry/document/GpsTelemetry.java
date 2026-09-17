package com.vibran.telemetry.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;

@Document(collection = "gps_telemetry")
@CompoundIndexes({
        // Primary query: all points for a vehicle sorted by time
        @CompoundIndex(name = "idx_vehicle_time",
                def = "{'vehicleId': 1, 'timestamp': -1}"),
        // Trip replay: all points for a trip in order
        @CompoundIndex(name = "idx_trip_time",
                def = "{'tripId': 1, 'timestamp': 1}"),
        // Device timeline
        @CompoundIndex(name = "idx_device_time",
                def = "{'deviceSerial': 1, 'timestamp': -1}")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GpsTelemetry {

    @Id
    private String id;

    // Links to MySQL vehicle.id — no FK in MongoDB by design
    @Field("vehicleId")
    private Long vehicleId;

    // Links to MySQL trip.id — null if vehicle is idling
    @Field("tripId")
    private Long tripId;

    @Field("deviceSerial")
    private String deviceSerial;

    // GeoJSON Point — required for 2dsphere geospatial queries
    // coordinates: [longitude, latitude] — GeoJSON order
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE,
            name  = "idx_location_geo")
    @Field("location")
    private GeoJsonPoint location;

    @Field("speed")
    private Double speed;         // km/h

    @Field("heading")
    private Integer heading;      // degrees 0-360

    @Field("altitude")
    private Integer altitude;     // metres above sea level

    @Field("ignition")
    private Boolean ignition;

    @Field("fuelLevel")
    private Double fuelLevel;     // 0-100% (fuel for ICE, battery for EV)

    @Field("batteryVoltage")
    private Double batteryVoltage;

    @Field("gpsAccuracy")
    private Double gpsAccuracy;   // metres — rejected above 50m

    @Field("satelliteCount")
    private Integer satelliteCount;

    // Duplicate detection — ESP32 increments this per packet
    @Field("sequenceId")
    @Indexed(unique = false)
    private Long sequenceId;

    // UTC timestamp — always UTC, never local time
    @Field("timestamp")
    private Instant timestamp;

    // ── Nested GeoJSON Point ──────────────────────────────────
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class GeoJsonPoint {
        private String type = "Point";
        // GeoJSON order is [longitude, latitude]
        private List<Double> coordinates;

        public static GeoJsonPoint of(double longitude, double latitude) {
            return GeoJsonPoint.builder()
                    .type("Point")
                    .coordinates(List.of(longitude, latitude))
                    .build();
        }

        public double getLongitude() { return coordinates.get(0); }
        public double getLatitude()  { return coordinates.get(1); }
    }
}
