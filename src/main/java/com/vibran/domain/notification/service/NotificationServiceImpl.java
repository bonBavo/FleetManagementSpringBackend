package com.vibran.domain.notification.service;

import com.vibran.domain.alert.entity.Alert;
import com.vibran.domain.notification.dto.response.NotificationResponse;
import com.vibran.domain.notification.entity.Notification;
import com.vibran.domain.notification.mapper.NotificationMapper;
import com.vibran.domain.notification.repository.NotificationRepository;
import com.vibran.domain.notification.service.interfaces.NotificationService;
import com.vibran.domain.user.entity.User;
import com.vibran.domain.user.repositiory.UserRepository;
import com.vibran.shared.enums.DeliveryStatus;
import com.vibran.shared.enums.NotificationChannel;
import com.vibran.shared.exception.ResourceNotFoundException;
import com.vibran.shared.response.PagedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final FcmService fcmService;
    private final NotificationMapper mapper;

    // ── Send alert notification ───────────────────────────────
    // @Async so it doesn't block the MQTT processing thread
    @Override
    @Async
    @Transactional
    public void sendAlertNotification(Alert alert) {
        User owner = alert.getVehicle().getOwner();

        String title = alert.getTitle();
        String body = alert.getMessage() != null
                ? alert.getMessage()
                : alert.getAlertType().name().replace('_', ' ');

        // 1. Attempt FCM push
        boolean pushed = false;
        if (owner.getFcmToken() != null) {
            pushed = fcmService.sendPush(
                    owner.getFcmToken(),
                    title, body,
                    alert.getSeverity(),
                    alert.getId(),
                    alert.getVehicle().getId()
            );
        }

        // 2. Save notification record regardless of push success
        //    This ensures it appears in the in-app notification list
        Notification notification = Notification.builder()
                .user(owner)
                .alert(alert)
                .title(title)
                .body(body)
                .channel(NotificationChannel.PUSH)
                .deliveryStatus(pushed ? DeliveryStatus.SENT : DeliveryStatus.FAILED)
                .failureReason(pushed ? null : "No FCM token registered")
                .sentAt(pushed ? Instant.now() : null)
                .build();

        notificationRepository.save(notification);

        log.info("Notification saved: alertId={}, userId={}, pushed={}",
                alert.getId(), owner.getId(), pushed);
    }

    // ── FCM token management ──────────────────────────────────
    @Override
    @Transactional
    public void updateFcmToken(Long userId, String fcmToken) {
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        user.setFcmToken(fcmToken);
        user.setFcmUpdatedAt(Instant.now());
        userRepository.save(user);

        log.info("FCM token updated for userId={}", userId);
    }

    // ── Queries ───────────────────────────────────────────────
    @Override
    public PagedResponse<NotificationResponse> getByUser(Long userId,
                                                         Pageable pageable) {
        Page<Notification> page = notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId, pageable);

        return PagedResponse.<NotificationResponse>builder()
                .content(page.getContent().stream()
                        .map(mapper::toResponse).toList())
                .page(page.getNumber()).size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages()).last(page.isLast())
                .build();
    }

    @Override
    public long countUnread(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    // ── Mark read ─────────────────────────────────────────────
    @Override
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        notificationRepository.markAsRead(
                notificationId, userId, Instant.now());
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsRead(userId, Instant.now());
    }
}