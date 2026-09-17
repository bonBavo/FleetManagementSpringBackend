package com.vibran.domain.trip.mapper;

import com.vibran.domain.trip.dto.request.StartTripRequest;
import com.vibran.domain.trip.dto.response.TripResponse;
import com.vibran.domain.trip.entity.Trip;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TripMapper {

    @Mapping(target = "id",       ignore = true)
    @Mapping(target = "vehicle",  ignore = true)
    @Mapping(target = "driver",   ignore = true)
    @Mapping(target = "device",   ignore = true)
    @Mapping(target = "status",   constant = "ACTIVE")
    @Mapping(target = "startTime",expression = "java(java.time.Instant.now())")
    @Mapping(target = "createdAt",ignore = true)
    @Mapping(target = "updatedAt",ignore = true)
    Trip toEntity(StartTripRequest request);

    @Mapping(target = "vehicleId",    source = "vehicle.id")
    @Mapping(target = "vehiclePlate", source = "vehicle.plateNumber")
    @Mapping(target = "driverName",   source = "driver.fullName")
    TripResponse toResponse(Trip trip);
}
