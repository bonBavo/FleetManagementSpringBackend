package com.vibran.domain.energy.entity;

import com.vibran.domain.trip.entity.Trip;
import com.vibran.domain.vehicle.entity.Vehicle;
import com.vibran.shared.enums.ChargerType;
import com.vibran.shared.enums.EnergyEventType;
import com.vibran.shared.enums.EnergyType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "energy_logs", indexes = {
        @Index(name = "idx_fuel_vehicle_id", columnList = "vehicle_id"),
        @Index(name = "idx_fuel_event_type", columnList = "event_type"),
        @Index(name = "idx_fuel_vehicle_time", columnList = "vehicle_id, recorded_at"),
        @Index(name = "idx_energy_type", columnList = "energy_type")
})
public class EnergyLog {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    private Trip trip;

    // ── ICE fields (null for EV) ──────────────────────────────
    @Column(name = "fuel_level_pct",  precision = 5, scale = 2)
    private BigDecimal fuelLevelPct;

    @Column(name = "fuel_volume_l",   precision = 8, scale = 3)
    private BigDecimal fuelVolumeL;

    // ── EV fields (null for ICE) ──────────────────────────────
    // Reuses fuelLevelPct as battery % — same 0-100 range
    @Column(name = "charge_kwh",      precision = 8, scale = 3)
    private BigDecimal chargeKwh;

    @Enumerated(EnumType.STRING)
    @Column(name = "charger_type",    length = 10)
    private ChargerType chargerType;

    // ── Common ────────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(name = "energy_type",     nullable = false, length = 20)
    @Builder.Default
    private EnergyType energyType = EnergyType.FUEL;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type",      nullable = false, length = 25)
    @Builder.Default
    private EnergyEventType eventType = EnergyEventType.PERIODIC_SNAPSHOT;

    @Column(name = "latitude",  precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(name = "recorded_at", nullable = false)
    private Instant recordedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
        if (recordedAt == null) recordedAt = Instant.now();
        if (energyType == null) energyType = EnergyType.FUEL;
        if (eventType == null) eventType = EnergyEventType.PERIODIC_SNAPSHOT;
        recordedAt = recordedAt != null ? recordedAt : Instant.now();
    }

    /** Returns battery % for EV, fuel % for ICE — same field */
    public BigDecimal getLevelPercent() {
        return fuelLevelPct;
    }
}
