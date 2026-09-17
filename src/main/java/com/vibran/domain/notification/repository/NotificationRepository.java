package com.vibran.domain.notification.repository;

import com.vibran.domain.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    // Flutter: notification list screen
    Page<Notification> findByUserIdOrderByCreatedAtDesc(
            Long userId, Pageable pageable);

    // Flutter: badge count (unread number on bell icon)
    long countByUserIdAndIsReadFalse(Long userId);

    // Mark single notification as read
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = :now " +
            "WHERE n.id = :id AND n.user.id = :userId")
    void markAsRead(@Param("id")     Long id,
                    @Param("userId") Long userId,
                    @Param("now")    Instant now);

    // Mark all as read — user taps "clear all"
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = :now " +
            "WHERE n.user.id = :userId AND n.isRead = false")
    void markAllAsRead(@Param("userId") Long userId,
                       @Param("now")    Instant now);

    // Cleanup: delete read notifications older than 30 days
    @Modifying
    @Query("DELETE FROM Notification n " +
            "WHERE n.isRead = true AND n.createdAt < :threshold")
    void deleteOldRead(@Param("threshold") Instant threshold);
}