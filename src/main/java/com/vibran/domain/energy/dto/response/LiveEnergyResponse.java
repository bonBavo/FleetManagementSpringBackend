package com.vibran.domain.energy.dto.response;

import com.vibran.shared.enums.EnergyType;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class LiveEnergyResponse {
    private Long       vehicleId;
    private EnergyType energyType;
    private Double     levelPercent;    // 0-100 — same field for fuel and battery
    private String     label;           // "Fuel" or "Battery"
}
