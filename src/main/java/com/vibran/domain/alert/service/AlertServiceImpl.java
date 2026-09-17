package com.vibran.domain.alert.service;

import com.vibran.domain.alert.service.interfaces.AlertService;


import com.vibran.config.WebSocketChannels;
import com.vibran.domain.alert.dto.request.AcknowledgeAlertRequest;
import com.vibran.domain.alert.dto.request.ResolveAlertRequest;
import com.vibran.domain.alert.dto.response.AlertResponse;
import com.vibran.domain.alert.dto.response.AlertSummaryResponse;
import com.vibran.domain.alert.entity.Alert;
import com.vibran.domain.alert.mapper.AlertMapper;
import com.vibran.domain.alert.repository.AlertRepository;
import com.vibran.domain.notification.service.interfaces.NotificationService;
import com.vibran.domain.user.entity.User;
import com.vibran.domain.user.repositiory.UserRepository;
import com.vibran.shared.exception.BusinessRuleException;
import com.vibran.shared.exception.ResourceNotFoundException;
import com.vibran.shared.response.PagedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;
    private final UserRepository userRepository;
    private final AlertMapper mapper;
    private final SimpMessagingTemplate messaging;
    private final NotificationService notificationService;

    // ── Save and push to Flutter ──────────────────────────────
    @Override
    @Transactional
    public void saveAndPush(List<Alert> alerts) {
        if (alerts == null || alerts.isEmpty()) return;

        alerts.forEach(alert -> {
            // Save to MySQL
            Alert saved = alertRepository.save(alert);
            log.info("Alert fired: type={}, vehicle={}, severity={}",
                    saved.getAlertType(),
                    saved.getVehicle().getPlateNumber(),
                    saved.getSeverity());

            // Push to vehicle WebSocket topic (app open)
            messaging.convertAndSend(
                    WebSocketChannels.vehicleAlerts(saved.getVehicle().getId()),
                    mapper.toSummaryResponse(saved));

            // Push to owner's personal WebSocket queue (app open)
            messaging.convertAndSendToUser(
                    saved.getVehicle().getOwner().getEmail(),
                    "/queue/notifications",
                    mapper.toSummaryResponse(saved));

            // Send FCM push notification (app closed) — runs async
            // so it never blocks the MQTT processing thread
            notificationService.sendAlertNotification(saved);
        });
    }

    // ── Queries ───────────────────────────────────────────────
    @Override
    public List<AlertSummaryResponse> getUnacknowledged(Long vehicleId) {
        return alertRepository
                .findByVehicleIdAndIsAcknowledgedFalseOrderByTriggeredAtDesc(vehicleId)
                .stream().map(mapper::toSummaryResponse).toList();
    }

    @Override
    public long countUnacknowledged(Long vehicleId) {
        return alertRepository.countByVehicleIdAndIsAcknowledgedFalse(vehicleId);
    }

    @Override
    public PagedResponse<AlertResponse> getHistory(Long vehicleId,
                                                   Pageable pageable) {
        Page<Alert> page = alertRepository
                .findByVehicleIdOrderByTriggeredAtDesc(vehicleId, pageable);
        return PagedResponse.<AlertResponse>builder()
                .content(page.getContent().stream()
                        .map(mapper::toResponse).toList())
                .page(page.getNumber()).size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages()).last(page.isLast())
                .build();
    }

    @Override
    public List<AlertSummaryResponse> getOwnerUnacknowledged(Long ownerId) {
        return alertRepository.findUnacknowledgedByOwner(ownerId)
                .stream().map(mapper::toSummaryResponse).toList();
    }

    // ── Acknowledge ───────────────────────────────────────────
    @Override
    @Transactional
    public AlertResponse acknowledge(Long alertId,
                                     AcknowledgeAlertRequest request) {
        Alert alert = findById(alertId);

        if (Boolean.TRUE.equals(alert.getIsAcknowledged()))
            throw new BusinessRuleException("Alert already acknowledged");

        User user = userRepository.findByIdAndIsDeletedFalse(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User", request.getUserId()));

        alert.setIsAcknowledged(true);
        alert.setAcknowledgedAt(Instant.now());
        alert.setAcknowledgedBy(user);

        return mapper.toResponse(alertRepository.save(alert));
    }

    // ── Resolve ───────────────────────────────────────────────
    @Override
    @Transactional
    public AlertResponse resolve(Long alertId, ResolveAlertRequest request) {
        Alert alert = findById(alertId);

        if (!Boolean.TRUE.equals(alert.getIsAcknowledged()))
            throw new BusinessRuleException(
                    "Alert must be acknowledged before it can be resolved");

        if (Boolean.TRUE.equals(alert.getIsResolved()))
            throw new BusinessRuleException("Alert already resolved");

        User user = userRepository.findByIdAndIsDeletedFalse(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User", request.getUserId()));

        alert.setIsResolved(true);
        alert.setResolvedAt(Instant.now());
        alert.setResolvedBy(user);
        alert.setResolutionNotes(request.getResolutionNotes());

        return mapper.toResponse(alertRepository.save(alert));
    }

    private Alert findById(Long id) {
        return alertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alert", id));
    }
}
