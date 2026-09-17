package com.vibran.domain.energy.dto.response;

import com.vibran.shared.enums.ChargerType;
import com.vibran.shared.enums.EnergyEventType;
import com.vibran.shared.enums.EnergyType;
import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;

@Data
public class EnergyLogResponse {
    private Long   id;
    private Long   vehicleId;
    private EnergyType      energyType;
    private EnergyEventType eventType;

    // ICE
    private BigDecimal fuelLevelPct;
    private BigDecimal fuelVolumeL;

    // EV
    private BigDecimal chargeKwh;
    private ChargerType chargerType;

    private Instant recordedAt;
}
