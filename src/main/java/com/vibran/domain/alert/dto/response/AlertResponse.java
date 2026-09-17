package com.vibran.domain.alert.dto.response;

import com.vibran.shared.enums.AlertSeverity;
import com.vibran.shared.enums.AlertType;
import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;

@Data
public class AlertResponse {
    private Long id;
    private Long vehicleId;
    private String vehiclePlate;
    private Long tripId;
    private AlertType alertType;
    private AlertSeverity severity;
    private String title;
    private String message;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private BigDecimal speedAtAlert;
    private BigDecimal thresholdValue;
    private BigDecimal actualValue;
    private Boolean isAcknowledged;
    private Instant acknowledgedAt;
    private String acknowledgedByName;
    private Boolean isResolved;
    private Instant resolvedAt;
    private String resolutionNotes;
    private Instant triggeredAt;
}

