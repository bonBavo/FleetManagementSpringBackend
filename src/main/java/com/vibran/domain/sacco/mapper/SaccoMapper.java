package com.vibran.domain.sacco.mapper;

import com.vibran.domain.sacco.dto.request.CreateSaccoRequest;
import com.vibran.domain.sacco.dto.response.SaccoMembershipResponse;
import com.vibran.domain.sacco.dto.response.SaccoResponse;
import com.vibran.domain.sacco.entity.Sacco;
import com.vibran.domain.sacco.entity.SaccoMembership;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SaccoMapper {

    @Mapping(target = "id",          ignore = true)
    @Mapping(target = "isActive",    constant = "true")
    @Mapping(target = "createdAt",   ignore = true)
    @Mapping(target = "updatedAt",   ignore = true)
    @Mapping(target = "memberships", ignore = true)
    Sacco toEntity(CreateSaccoRequest request);

    @Mapping(target = "memberCount", ignore = true) // set in service
    SaccoResponse toResponse(Sacco sacco);

    @Mapping(target = "saccoId",     source = "sacco.id")
    @Mapping(target = "saccoName",   source = "sacco.name")
    @Mapping(target = "vehicleId",   source = "vehicle.id")
    @Mapping(target = "vehiclePlate",source = "vehicle.plateNumber")
    @Mapping(target = "ownerName",   source = "owner.fullName")
    SaccoMembershipResponse toMembershipResponse(SaccoMembership membership);
}
