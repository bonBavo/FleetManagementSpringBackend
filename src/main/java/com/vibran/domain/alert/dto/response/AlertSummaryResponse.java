package com.vibran.domain.alert.dto.response;

import com.vibran.shared.enums.AlertSeverity;
import com.vibran.shared.enums.AlertType;
import lombok.Data;
import java.time.Instant;

@Data
public class AlertSummaryResponse {
    private Long          id;
    private Long          vehicleId;
    private String        vehiclePlate;
    private AlertType     alertType;
    private AlertSeverity severity;
    private String        title;
    private Boolean       isAcknowledged;
    private Instant triggeredAt;
}