package com.vibran.domain.notification.service;
// Runs nightly to delete old read notifications
// Keeps the notifications table from growing indefinitely

import com.vibran.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationCleanupScheduler {

    private final NotificationRepository notificationRepository;

    // Runs every day at 2am
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void deleteOldNotifications() {
        Instant threshold = Instant.now().minusSeconds(30 * 24 * 60 * 60);
        notificationRepository.deleteOldRead(threshold);
        log.info("Cleaned up read notifications older than 30 days");
    }
}
