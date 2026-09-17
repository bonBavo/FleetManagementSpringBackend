package com.vibran.domain.device.entity;

import com.vibran.domain.vehicle.entity.Vehicle;
import com.vibran.shared.enums.DeviceStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "device_registry", indexes = {
        @Index(name = "idx_device_vehicle_id",     columnList = "vehicle_id"),
        @Index(name = "idx_device_status",         columnList = "status"),
        @Index(name = "idx_device_vehicle_status", columnList = "vehicle_id,status"),
        @Index(name = "idx_device_last_seen",      columnList = "last_seen_at")
})
public class DeviceRegistry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @Column(name = "device_serial", nullable = false, length = 100)
    private String deviceSerial;

    @Column(name = "sim_iccid", length = 30)
    private String simIccid;

    @Column(name = "sim_phone_number", length = 20)
    private String simPhoneNumber;

    @Column(name = "device_token", nullable = false, length = 255)
    private String deviceToken;

    @Column(name = "token_expires_at")
    private Instant tokenExpiresAt;

    @Column(name = "firmware_version", length = 50)
    private String firmwareVersion;

    @Column(name = "last_seen_at")
    private Instant lastSeenAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    @Builder.Default
    private DeviceStatus status = DeviceStatus.UNASSIGNED;

    @Column(name = "assigned_at")
    private Instant assignedAt;

    @Column(name = "deactivated_at")
    private Instant deactivatedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
        if (updatedAt == null) updatedAt = Instant.now();
        if (status == null) status = DeviceStatus.UNASSIGNED;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
