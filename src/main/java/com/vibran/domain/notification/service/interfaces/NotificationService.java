package com.vibran.domain.notification.service.interfaces;

import com.vibran.domain.alert.entity.Alert;
import com.vibran.domain.notification.dto.response.NotificationResponse;
import com.vibran.shared.response.PagedResponse;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    // Called by AlertService after saving an alert
    void sendAlertNotification(Alert alert);

    // Flutter: update FCM token after login or app restart
    void updateFcmToken(Long userId, String fcmToken);

    // Flutter: notification list screen
    PagedResponse<NotificationResponse> getByUser(Long userId, Pageable pageable);

    // Flutter: badge count on bell icon
    long countUnread(Long userId);

    // Flutter: tap on single notification
    void markAsRead(Long notificationId, Long userId);

    // Flutter: clear all button
    void markAllAsRead(Long userId);
}
