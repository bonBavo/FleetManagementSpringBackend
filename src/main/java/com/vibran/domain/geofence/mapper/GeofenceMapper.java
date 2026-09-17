package com.vibran.domain.geofence.mapper;

import com.vibran.domain.geofence.dto.request.CreateGeofenceRequest;
import com.vibran.domain.geofence.dto.request.UpdateGeofenceRequest;
import com.vibran.domain.geofence.dto.response.GeofenceResponse;
import com.vibran.domain.geofence.entity.Geofence;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface GeofenceMapper {

    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "owner",     ignore = true)
    @Mapping(target = "vehicle",   ignore = true)
    @Mapping(target = "isActive",  constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Geofence toEntity(CreateGeofenceRequest request);

    @Mapping(target = "ownerId",     source = "owner.id")
    @Mapping(target = "ownerName",   source = "owner.fullName")
    @Mapping(target = "vehicleId",   source = "vehicle.id")
    @Mapping(target = "vehiclePlate",source = "vehicle.plateNumber")
    GeofenceResponse toResponse(Geofence geofence);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "owner",     ignore = true)
    @Mapping(target = "vehicle",   ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateFromRequest(UpdateGeofenceRequest request,
                           @MappingTarget Geofence geofence);
}
