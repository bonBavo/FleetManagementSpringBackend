package com.vibran.domain.alert.mapper;

import com.vibran.domain.alert.dto.response.AlertResponse;
import com.vibran.domain.alert.dto.response.AlertSummaryResponse;
import com.vibran.domain.alert.entity.Alert;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface AlertMapper {

    @Mapping(target = "vehicleId",         source = "vehicle.id")
    @Mapping(target = "vehiclePlate",      source = "vehicle.plateNumber")
    @Mapping(target = "tripId",            source = "trip.id")
    @Mapping(target = "acknowledgedByName",source = "acknowledgedBy.fullName")
    AlertResponse toResponse(Alert alert);

    @Mapping(target = "vehicleId",    source = "vehicle.id")
    @Mapping(target = "vehiclePlate", source = "vehicle.plateNumber")
    AlertSummaryResponse toSummaryResponse(Alert alert);
}