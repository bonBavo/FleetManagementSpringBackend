package com.vibran.domain.user.mapper;


import com.vibran.domain.user.dto.request.CreateUserRequest;
import com.vibran.domain.user.dto.request.UpdateUserProfileRequest;
import com.vibran.domain.user.dto.request.UpdateUserRequest;
import com.vibran.domain.user.dto.response.UserProfileResponse;
import com.vibran.domain.user.dto.response.UserResponse;
import com.vibran.domain.user.dto.response.UserSummaryResponse;
import com.vibran.domain.user.dto.response.UserWithProfileResponse;
import com.vibran.domain.user.entity.User;
import com.vibran.domain.user.entity.UserProfile;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id",              ignore = true)
    @Mapping(target = "passwordHash",    ignore = true)
    @Mapping(target = "isActive",        constant = "true")
    @Mapping(target = "isDeleted",       constant = "false")
    @Mapping(target = "isEmailVerified", constant = "false")
    @Mapping(target = "createdAt",       ignore = true)
    @Mapping(target = "updatedAt",       ignore = true)
    @Mapping(target = "deletedAt",       ignore = true)
    @Mapping(target = "suspendedAt",     ignore = true)
    @Mapping(target = "suspendedReason", ignore = true)
    @Mapping(target = "profile",         ignore = true)
    User toEntity(CreateUserRequest request);

    UserResponse toResponse(User user);

    UserProfileResponse toProfileResponse(UserProfile profile);

    @Mapping(target = "profilePhotoUrl",
            source = "profile.profilePhotoUrl")
    UserSummaryResponse toSummaryResponse(User user);

    @Mapping(target = "profile", source = "profile")
    UserWithProfileResponse toWithProfileResponse(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id",              ignore = true)
    @Mapping(target = "email",           ignore = true)
    @Mapping(target = "passwordHash",    ignore = true)
    @Mapping(target = "role",            ignore = true)
    @Mapping(target = "isActive",        ignore = true)
    @Mapping(target = "isDeleted",       ignore = true)
    @Mapping(target = "isEmailVerified", ignore = true)
    @Mapping(target = "createdAt",       ignore = true)
    @Mapping(target = "updatedAt",       ignore = true)
    @Mapping(target = "deletedAt",       ignore = true)
    @Mapping(target = "suspendedAt",     ignore = true)
    @Mapping(target = "suspendedReason", ignore = true)
    @Mapping(target = "profile",         ignore = true)
    void updateEntityFromRequest(UpdateUserRequest request,
                                 @MappingTarget User user);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "user",      ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateProfileFromRequest(UpdateUserProfileRequest request,
                                  @MappingTarget UserProfile profile);
}
