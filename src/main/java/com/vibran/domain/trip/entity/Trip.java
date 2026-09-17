package com.vibran.domain.trip.entity;

import com.vibran.domain.device.entity.DeviceRegistry;
import com.vibran.domain.user.entity.User;
import com.vibran.domain.vehicle.entity.Vehicle;
import com.vibran.shared.enums.TripStatus;
import com.vibran.shared.enums.TripType;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.Instant;

@Entity
@Table(name = "trips", indexes = {
        @Index(name = "idx_trips_vehicle_id",     columnList = "vehicle_id"),
        @Index(name = "idx_trips_driver_id",      columnList = "driver_id"),
        @Index(name = "idx_trips_status",         columnList = "status"),
        @Index(name = "idx_trips_start_time",     columnList = "start_time"),
        @Index(name = "idx_trips_vehicle_status", columnList = "vehicle_id,status"),
        @Index(name = "idx_trips_route_code",     columnList = "route_code")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Trip {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private User driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private DeviceRegistry device;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time")
    private Instant endTime;

    @Column(name = "start_latitude",  precision = 10, scale = 8)
    private BigDecimal startLatitude;

    @Column(name = "start_longitude", precision = 11, scale = 8)
    private BigDecimal startLongitude;

    @Column(name = "end_latitude",    precision = 10, scale = 8)
    private BigDecimal endLatitude;

    @Column(name = "end_longitude",   precision = 11, scale = 8)
    private BigDecimal endLongitude;

    @Column(name = "start_address", length = 500)
    private String startAddress;

    @Column(name = "end_address", length = 500)
    private String endAddress;

    @Column(name = "distance_km",      precision = 10, scale = 3)
    private BigDecimal distanceKm;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "max_speed_kmh",    precision = 6, scale = 2)
    private BigDecimal maxSpeedKmh;

    @Column(name = "avg_speed_kmh",    precision = 6, scale = 2)
    private BigDecimal avgSpeedKmh;

    @Column(name = "fuel_consumed_l",  precision = 8, scale = 3)
    private BigDecimal fuelConsumedL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    @Builder.Default
    private TripStatus status = TripStatus.ACTIVE;

    // ── Matatu-specific ───────────────────────────────────────
    @Column(name = "route_code", length = 50)
    private String routeCode;

    @Column(name = "passenger_count")
    private Integer passengerCount;

    @Column(name = "fare_collected_kes", precision = 10, scale = 2)
    private BigDecimal fareCollectedKes;

    @Enumerated(EnumType.STRING)
    @Column(name = "trip_type", nullable = false, length = 15)
    @Builder.Default
    private TripType tripType = TripType.PRIVATE;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;


    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
        if (updatedAt == null) updatedAt = Instant.now();
        if (status == null) status = TripStatus.ACTIVE;
        if (tripType == null) tripType = TripType.PRIVATE;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
