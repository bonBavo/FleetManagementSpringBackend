package com.vibran.domain.vehicle.entity;

import com.vibran.domain.user.entity.User;
import com.vibran.shared.enums.PowertrainType;
import com.vibran.shared.enums.VehicleCategory;
import com.vibran.shared.enums.VehicleStatus;
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
@Table(name = "vehicles", indexes = {
        @Index(name = "idx_vehicles_owner_id", columnList = "owner_id"),
        @Index(name = "idx_vehicles_plate", columnList = "plate_number"),
        @Index(name = "idx_vehicles_status", columnList = "status"),
        @Index(name = "idx_vehicles_is_deleted", columnList = "is_deleted"),
        @Index(name = "idx_vehicles_powertrain", columnList = "powertrain_type"),
        @Index(name = "idx_vehicles_category", columnList = "vehicle_category")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uq_vehicles_plate", columnNames = {"plate_number"}),
        @UniqueConstraint(name = "uq_vehicles_vin", columnNames = {"vin"})
})
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", nullable = false)
    private VehicleModel model;

    @Column(name = "plate_number", nullable = false, length = 20)
    private String plateNumber;

    @Column(nullable = false)
    private Integer year;

    @Column(length = 50)
    private String color;

    @Enumerated(EnumType.STRING)
    @Column(name = "powertrain_type", nullable = false)
    @Builder.Default
    private PowertrainType powertrainType = PowertrainType.ICE;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_category", nullable = false)
    @Builder.Default
    private VehicleCategory vehicleCategory = VehicleCategory.PERSONAL_CAR;

    @Column(name = "seating_capacity")
    private Integer seatingCapacity;

    @Column(length = 50)
    private String vin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private VehicleStatus status = VehicleStatus.ACTIVE;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    public boolean isElectric() {
        return powertrainType == PowertrainType.ELECTRIC
                || powertrainType == PowertrainType.HYBRID;
    }

    public boolean isMatatu() {
        return vehicleCategory == VehicleCategory.MATATU_14
                || vehicleCategory == VehicleCategory.MATATU_33;
    }


    @PrePersist
    protected void onCreate() {
        if (isDeleted == null) isDeleted = false;
        if (status == null) status = VehicleStatus.ACTIVE;
        if (powertrainType == null) powertrainType = PowertrainType.ICE;
        if (vehicleCategory == null) vehicleCategory = VehicleCategory.PERSONAL_CAR;
    }
}
