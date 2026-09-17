package com.vibran.domain.device.mapper;

import com.vibran.domain.device.dto.request.RegisterDeviceRequest;
import com.vibran.domain.device.dto.response.DeviceResponse;
import com.vibran.domain.device.entity.DeviceRegistry;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DeviceMapper {

    @Mapping(target = "id",          ignore = true)
    @Mapping(target = "vehicle",     ignore = true)
    @Mapping(target = "status",      constant = "UNASSIGNED")
    @Mapping(target = "createdAt",   ignore = true)
    @Mapping(target = "updatedAt",   ignore = true)
    @Mapping(target = "assignedAt",  ignore = true)
    @Mapping(target = "lastSeenAt",  ignore = true)
    DeviceRegistry toEntity(RegisterDeviceRequest request);

    @Mapping(target = "vehicleId",    source = "vehicle.id")
    @Mapping(target = "vehiclePlate", source = "vehicle.plateNumber")
    DeviceResponse toResponse(DeviceRegistry device);
}
