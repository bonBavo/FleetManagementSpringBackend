package com.vibran.domain.notification.entity;

import com.vibran.domain.alert.entity.Alert;
import com.vibran.domain.user.entity.User;
import com.vibran.shared.enums.DeliveryStatus;
import com.vibran.shared.enums.NotificationChannel;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "notifications", indexes = {
        @Index(name = "idx_notif_user_id",     columnList = "user_id"),
        @Index(name = "idx_notif_is_read",     columnList = "is_read"),
        @Index(name = "idx_notif_user_unread", columnList = "user_id,is_read,created_at")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Notification {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Source alert — nullable (some notifications are system-generated)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alert_id")
    private Alert alert;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private NotificationChannel channel = NotificationChannel.PUSH;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", nullable = false, length = 12)
    private DeliveryStatus deliveryStatus = DeliveryStatus.PENDING;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    @Column(name = "sent_at")
    private Instant sentAt;

    @Column(name = "delivered_at")
    private Instant deliveredAt;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "read_at")
    private Instant readAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist protected void onCreate() {
        if (isRead == null) isRead = false;
        if (channel == null) channel = NotificationChannel.PUSH;
        if (deliveryStatus == null) deliveryStatus = DeliveryStatus.PENDING;
    }
}