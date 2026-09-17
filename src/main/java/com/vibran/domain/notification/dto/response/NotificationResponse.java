package com.vibran.domain.notification.dto.response;

import com.vibran.shared.enums.DeliveryStatus;
import com.vibran.shared.enums.NotificationChannel;
import lombok.Data;
import java.time.Instant;

@Data
public class NotificationResponse {
    private Long id;
    private Long alertId;
    private String title;
    private String body;
    private NotificationChannel channel;
    private DeliveryStatus deliveryStatus;
    private Boolean isRead;
    private Instant readAt;
    private Instant createdAt;
}

