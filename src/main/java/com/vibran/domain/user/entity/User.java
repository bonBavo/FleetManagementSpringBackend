package com.vibran.domain.user.entity;

import com.vibran.domain.user.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_users_email",
                columnList = "email"),
        @Index(name = "idx_users_role",
                columnList = "role"),
        @Index(name = "idx_users_is_active",
                columnList = "is_active"),
        @Index(name = "idx_users_is_deleted",
                columnList = "is_deleted")}, uniqueConstraints = {
        @UniqueConstraint(name = "uq_users_email",
                columnNames = {"email"}),
        @UniqueConstraint(name = "uq_users_phone",
                columnNames = {"phone"})})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @ColumnDefault("'DRIVER'")
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole role;

    @ColumnDefault("1")
    @Column(name = "is_active")
    private Boolean isActive;

    @ColumnDefault("0")
    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @ColumnDefault("0")
    @Column(name = "is_email_verified")
    private Boolean isEmailVerified;

    @Column(name = "suspended_at")
    private Instant suspendedAt;

    @Column(name = "suspended_reason")
    private String suspendedReason;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "fcm_token", length = 500)
    private String fcmToken;

    @Column(name = "fcm_updated_at")
    private Instant fcmUpdatedAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserProfile profile;

    @PrePersist
    protected void onCreate() {
        if (role == null) role = UserRole.DRIVER;
        if (isActive == null) isActive = true;
        if (isDeleted == null) isDeleted = false;
        if (isEmailVerified == null) isEmailVerified = false;
    }

}