package com.vibran.domain.vehicle.mapper;

import com.vibran.domain.vehicle.dto.request.RegisterVehicleRequest;
import com.vibran.domain.vehicle.dto.request.UpdateVehicleRequest;
import com.vibran.domain.vehicle.dto.response.VehicleResponse;
import com.vibran.domain.vehicle.dto.response.VehicleSummaryResponse;
import com.vibran.domain.vehicle.entity.Vehicle;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VehicleMapper {

    @Mapping(target = "id",          ignore = true)
    @Mapping(target = "owner",       ignore = true)
    @Mapping(target = "model",       ignore = true)
    @Mapping(target = "isDeleted",   constant = "false")
    @Mapping(target = "createdAt",   ignore = true)
    @Mapping(target = "updatedAt",   ignore = true)
    @Mapping(target = "deletedAt",   ignore = true)
    Vehicle toEntity(RegisterVehicleRequest request);

    @Mapping(target = "makeName",  source = "model.make.name")
    @Mapping(target = "modelName", source = "model.name")
    @Mapping(target = "ownerName", source = "owner.fullName")
    VehicleResponse toResponse(Vehicle vehicle);

    @Mapping(target = "makeName",  source = "model.make.name")
    @Mapping(target = "modelName", source = "model.name")
    VehicleSummaryResponse toSummaryResponse(Vehicle vehicle);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id",           ignore = true)
    @Mapping(target = "plateNumber",  ignore = true)
    @Mapping(target = "owner",        ignore = true)
    @Mapping(target = "model",        ignore = true)
    @Mapping(target = "year",         ignore = true)
    @Mapping(target = "createdAt",    ignore = true)
    @Mapping(target = "updatedAt",    ignore = true)
    void updateFromRequest(UpdateVehicleRequest request,
                           @MappingTarget Vehicle vehicle);
}
