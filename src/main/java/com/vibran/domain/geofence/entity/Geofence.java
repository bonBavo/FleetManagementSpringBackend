package com.vibran.domain.geofence.entity;

import com.vibran.domain.user.entity.User;
import com.vibran.domain.vehicle.entity.Vehicle;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "geofences", indexes = {
    @Index(name = "idx_geofences_owner_id",   columnList = "owner_id"),
    @Index(name = "idx_geofences_vehicle_id", columnList = "vehicle_id"),
    @Index(name = "idx_geofences_is_active",  columnList = "is_active")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Geofence {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    // NULL = applies to ALL vehicles owned by this user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(name = "center_latitude", nullable = false, precision = 10, scale = 8)
    private BigDecimal centerLatitude;

    @Column(name = "center_longitude", nullable = false, precision = 11, scale = 8)
    private BigDecimal centerLongitude;

    // metres — min 50, max 50000
    @Column(name = "radius_meters", nullable = false)
    private Integer radiusMeters;

    @Column(name = "alert_on_entry", nullable = false)
    @Builder.Default private Boolean alertOnEntry = true;

    @Column(name = "alert_on_exit", nullable = false)
    @Builder.Default private Boolean alertOnExit  = true;

    @Column(name = "is_active", nullable = false)
    @Builder.Default private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist protected void onCreate() {
        if (alertOnEntry == null) alertOnEntry = true;
        if (alertOnExit == null) alertOnExit = true;
        if (isActive == null) isActive = true;
    }
}
