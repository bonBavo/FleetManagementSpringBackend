    package com.vibran.domain.energy.service.interfaces;

import com.vibran.domain.energy.dto.response.EnergyLogResponse;
import com.vibran.domain.energy.dto.response.LiveEnergyResponse;

import java.time.Instant;
import java.util.List;

public interface EnergyService {
    LiveEnergyResponse    getLiveLevel(Long vehicleId);
    List<EnergyLogResponse> getHistory(Long vehicleId,
                                       Instant from,
                                       Instant to);
}
