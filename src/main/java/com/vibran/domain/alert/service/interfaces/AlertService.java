package com.vibran.domain.alert.service.interfaces;

import com.vibran.domain.alert.dto.request.AcknowledgeAlertRequest;
import com.vibran.domain.alert.dto.request.ResolveAlertRequest;
import com.vibran.domain.alert.dto.response.AlertResponse;
import com.vibran.domain.alert.dto.response.AlertSummaryResponse;
import com.vibran.domain.alert.entity.Alert;
import com.vibran.shared.response.PagedResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AlertService {

    // Called by TelemetryService with pre-built alert objects from rules engine
    void saveAndPush(List<Alert> alerts);

    // Flutter: unacknowledged alerts for a vehicle
    List<AlertSummaryResponse> getUnacknowledged(Long vehicleId);

    // Flutter: unread badge count
    long countUnacknowledged(Long vehicleId);

    // Flutter: full paginated history
    PagedResponse<AlertResponse> getHistory(Long vehicleId, Pageable pageable);

    // Flutter: all unacknowledged across owner's fleet
    List<AlertSummaryResponse> getOwnerUnacknowledged(Long ownerId);

    // Acknowledge
    AlertResponse acknowledge(Long alertId, AcknowledgeAlertRequest request);

    // Resolve
    AlertResponse resolve(Long alertId, ResolveAlertRequest request);
}
