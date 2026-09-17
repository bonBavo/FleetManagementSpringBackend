package com.vibran.domain.notification.controller;

import com.vibran.domain.notification.dto.request.UpdateFcmTokenRequest;
import com.vibran.domain.notification.dto.response.NotificationResponse;
import com.vibran.domain.notification.service.interfaces.NotificationService;
import com.vibran.shared.response.ApiResponse;
import com.vibran.shared.response.PagedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // GET /api/v1/notifications?userId=1&page=0&size=20
    // Flutter: notification list screen
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<NotificationResponse>>> getAll(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable p = PageRequest.of(page, size,
                Sort.by("createdAt").descending());
        return ResponseEntity.ok(
                ApiResponse.success(
                        notificationService.getByUser(userId, p)));
    }

    // GET /api/v1/notifications/count?userId=1
    // Flutter: badge count on bell icon
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(
            @RequestParam Long userId) {
        return ResponseEntity.ok(
                ApiResponse.success(notificationService.countUnread(userId)));
    }

    // POST /api/v1/notifications/{id}/read?userId=1
    // Flutter: user taps a notification
    @PostMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable Long id,
            @RequestParam Long userId) {
        notificationService.markAsRead(id, userId);
        return ResponseEntity.ok(
                ApiResponse.success("Notification marked as read", null));
    }

    // POST /api/v1/notifications/read-all?userId=1
    // Flutter: user taps "clear all"
    @PostMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            @RequestParam Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(
                ApiResponse.success("All notifications marked as read", null));
    }

    // PUT /api/v1/notifications/fcm-token?userId=1
    // Flutter: sends FCM token after login or app restart
    @PutMapping("/fcm-token")
    public ResponseEntity<ApiResponse<Void>> updateFcmToken(
            @RequestParam Long userId,
            @Valid @RequestBody UpdateFcmTokenRequest request) {
        notificationService.updateFcmToken(userId, request.getFcmToken());
        return ResponseEntity.ok(
                ApiResponse.success("FCM token updated", null));
    }
}
