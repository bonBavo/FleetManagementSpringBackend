package com.vibran.domain.sacco.entity;

import com.vibran.domain.user.entity.User;
import com.vibran.domain.vehicle.entity.Vehicle;
import com.vibran.shared.enums.SaccoMemberRole;
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
@Table(name = "sacco_memberships", indexes = {
        @Index(name = "idx_membership_sacco_id", columnList = "sacco_id"),
        @Index(name = "idx_membership_vehicle_id", columnList = "vehicle_id"),
        @Index(name = "idx_membership_is_active", columnList = "is_active")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uq_active_vehicle_sacco", columnNames = {"vehicle_id", "is_active"})
})
public class SaccoMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sacco_id", nullable = false)
    private Sacco sacco;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SaccoMemberRole role;

    @Column(name = "route_code", length = 50)
    private String routeCode;

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt;

    @Column(name = "left_at")
    private Instant leftAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

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
        if (joinedAt == null) joinedAt = Instant.now();
        if (isActive == null) isActive = true;
        if (role == null) role = SaccoMemberRole.MEMBER;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
