package com.vibran.domain.energy.mapper;

import com.vibran.domain.energy.dto.response.EnergyLogResponse;
import com.vibran.domain.energy.entity.EnergyLog;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EnergyMapper {

    @Mapping(target = "vehicleId", source = "vehicle.id")
    EnergyLogResponse toResponse(EnergyLog log);
}
