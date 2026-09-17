package com.vibran.domain.notification.service;

import com.google.firebase.messaging.*;
import com.vibran.shared.enums.AlertSeverity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FcmService {

    /**
     * Sends a push notification to a single device.
     * Returns true if successful, false if failed.
     *
     * @param fcmToken   the device's FCM registration token
     * @param title      notification title shown on phone
     * @param body       notification body text
     * @param severity   used to set notification colour on Android
     * @param alertId    passed as data so Flutter can deep-link to alert
     * @param vehicleId  passed as data for Flutter routing
     */
    public boolean sendPush(String fcmToken,
                            String title,
                            String body,
                            AlertSeverity severity,
                            Long alertId,
                            Long vehicleId) {

        if (fcmToken == null || fcmToken.isBlank()) {
            log.warn("Cannot send FCM push — no token registered for user");
            return false;
        }

        try {
            Message message = Message.builder()
                    .setToken(fcmToken)

                    // ── Notification payload (shown on lock screen) ──
                    .setNotification(
                            com.google.firebase.messaging.Notification.builder()
                                    .setTitle(title)
                                    .setBody(body)
                                    .build())

                    // ── Data payload (Flutter reads these in background) ─
                    // Flutter uses these to navigate to the right screen
                    .putData("alertId",   alertId   != null ? alertId.toString()   : "")
                    .putData("vehicleId", vehicleId != null ? vehicleId.toString() : "")
                    .putData("severity",  severity  != null ? severity.name()       : "")
                    .putData("click_action", "FLUTTER_NOTIFICATION_CLICK")

                    // ── Android config ────────────────────────────────
                    .setAndroidConfig(
                            AndroidConfig.builder()
                                    .setPriority(AndroidConfig.Priority.HIGH)
                                    .setNotification(
                                            AndroidNotification.builder()
                                                    .setColor(severityColor(severity))
                                                    .setSound("default")
                                                    .setChannelId("fleet_alerts")
                                                    .build())
                                    .build())

                    // ── APNS (iOS) config ─────────────────────────────
                    .setApnsConfig(
                            ApnsConfig.builder()
                                    .setAps(Aps.builder()
                                            .setSound("default")
                                            .setBadge(1)
                                            .build())
                                    .build())

                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            log.info("FCM push sent: messageId={}, alertId={}", response, alertId);
            return true;

        } catch (FirebaseMessagingException e) {
            log.error("FCM push failed: code={}, message={}",
                    e.getMessagingErrorCode(), e.getMessage());
            return false;
        }
    }

    // Map severity to Android notification colour
    private String severityColor(AlertSeverity severity) {
        if (severity == null) return "#0D7377";
        return switch (severity) {
            case CRITICAL -> "#DC2626";   // red
            case HIGH     -> "#D97706";   // amber
            case MEDIUM   -> "#0D7377";   // teal
            case LOW      -> "#16A34A";   // green
            case INFO     -> "#6B7280";   // grey
        };
    }
}
