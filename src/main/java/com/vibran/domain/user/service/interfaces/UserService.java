package com.vibran.domain.user.service.interfaces;

import com.vibran.domain.user.dto.request.*;
import com.vibran.domain.user.dto.response.UserProfileResponse;
import com.vibran.domain.user.dto.response.UserResponse;
import com.vibran.domain.user.dto.response.UserSummaryResponse;
import com.vibran.domain.user.dto.response.UserWithProfileResponse;
import com.vibran.domain.user.enums.UserRole;
import com.vibran.shared.response.PagedResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

public interface UserService {


    // CRUD
    UserResponse createUser(CreateUserRequest request);
    UserWithProfileResponse getById(Long id);
    UserResponse update(Long id, UpdateUserRequest request);
    void softDelete(Long id);



    // Profile
    UserProfileResponse  updateProfile(Long id, UpdateUserProfileRequest request);

    // Admin operations
    void                 suspend(Long id, SuspendUserRequest request);
    void                 unsuspend(Long id);
    void                 changePassword(Long id, ChangePasswordRequest request);

    // Lists — Flutter screens
    PagedResponse<UserResponse> getAllUsers(Pageable pageable);
    PagedResponse<UserResponse>        getUsersByRole(UserRole role, Pageable pageable);
    PagedResponse<UserSummaryResponse>          getDrivers();     // driver assignment dropdown}
}