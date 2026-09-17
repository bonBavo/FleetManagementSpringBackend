package com.vibran.domain.vehicle.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "vehicle_makes", indexes = {
        @Index(name = "idx_vehicle_makes_name", columnList = "name")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uq_vehicle_makes_name", columnNames = {"name"})
})
public class VehicleMake {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "country_of_origin", length = 100)
    private String countryOfOrigin;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "make", fetch = FetchType.LAZY)
    @Builder.Default
    private List<VehicleModel> models = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
