package com.vibran.domain.alert.entity;

import com.vibran.domain.device.entity.DeviceRegistry;
import com.vibran.domain.trip.entity.Trip;
import com.vibran.domain.user.entity.User;
import com.vibran.domain.vehicle.entity.Vehicle;
import com.vibran.shared.enums.AlertSeverity;
import com.vibran.shared.enums.AlertType;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "alerts", indexes = {
        @Index(name = "idx_alerts_vehicle_id",      columnList = "vehicle_id"),
        @Index(name = "idx_alerts_type",            columnList = "alert_type"),
        @Index(name = "idx_alerts_severity",        columnList = "severity"),
        @Index(name = "idx_alerts_is_acknowledged", columnList = "is_acknowledged"),
        @Index(name = "idx_alerts_triggered_at",    columnList = "triggered_at"),
        @Index(name = "idx_alerts_vehicle_ack_sev", columnList = "vehicle_id,is_acknowledged,severity"),
        @Index(name = "idx_alerts_throttle",        columnList = "vehicle_id,alert_type,cooldown_until")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Alert {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id")
    private DeviceRegistry device;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acknowledged_by")
    private User acknowledgedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_by")
    private User resolvedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type", nullable = false, length = 30)
    private AlertType alertType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @Builder.Default
    private AlertSeverity severity = AlertSeverity.MEDIUM;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(name = "latitude",        precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(name = "longitude",       precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(name = "speed_at_alert",  precision = 6, scale = 2)
    private BigDecimal speedAtAlert;

    @Column(name = "threshold_value", precision = 10, scale = 2)
    private BigDecimal thresholdValue;

    @Column(name = "actual_value",    precision = 10, scale = 2)
    private BigDecimal actualValue;

    @Column(name = "is_acknowledged", nullable = false)
    @Builder.Default private Boolean isAcknowledged = false;

    @Column(name = "acknowledged_at")
    private Instant acknowledgedAt;

    @Column(name = "is_resolved", nullable = false)
    @Builder.Default private Boolean isResolved = false;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    @Column(name = "resolution_notes", length = 1000)
    private String resolutionNotes;

    @Column(name = "cooldown_until")
    private Instant cooldownUntil;

    @Column(name = "triggered_at", nullable = false)
    private Instant triggeredAt;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist protected void onCreate() {
        createdAt   = Instant.now();
        updatedAt   = Instant.now();
        triggeredAt = triggeredAt != null ? triggeredAt : Instant.now();
    }
    @PreUpdate protected void onUpdate() { updatedAt = Instant.now(); }
}