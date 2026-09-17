package com.vibran.domain.vehicle.entity;

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
@Table(name = "vehicle_models", indexes = {
        @Index(name = "idx_vehicle_models_make_id", columnList = "make_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uq_vehicle_model_make", columnNames = {"make_id", "name"})
})
public class VehicleModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "make_id", nullable = false)
    private VehicleMake make;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "fuel_capacity_l", precision = 6, scale = 2)
    private BigDecimal fuelCapacityL;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
