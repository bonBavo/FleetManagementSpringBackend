package com.vibran.domain.user.service.impl;

import com.vibran.domain.user.dto.request.*;
import com.vibran.domain.user.dto.response.UserProfileResponse;
import com.vibran.domain.user.dto.response.UserResponse;
import com.vibran.domain.user.dto.response.UserSummaryResponse;
import com.vibran.domain.user.dto.response.UserWithProfileResponse;
import com.vibran.domain.user.entity.User;
import com.vibran.domain.user.entity.UserProfile;
import com.vibran.domain.user.enums.UserRole;
import com.vibran.domain.user.mapper.UserMapper;
import com.vibran.domain.user.repositiory.UserProfileRepository;
import com.vibran.domain.user.repositiory.UserRepository;
import com.vibran.domain.user.service.interfaces.UserService;
import com.vibran.shared.exception.BusinessRuleException;
import com.vibran.shared.exception.DuplicateResourceException;
import com.vibran.shared.exception.ResourceNotFoundException;
import com.vibran.shared.response.PagedResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository profileRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "Email already registered: " + request.getEmail());
        }

        if (userRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException(
                    "Phone number already registered: " + request.getPhone());
        }

        User user = userMapper.toEntity(request);

        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);
        log.info("User created: id={}", savedUser);

        UserProfile profile = UserProfile.builder()
                .user(savedUser)
                .build();
        profileRepository.save(profile);

        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserWithProfileResponse getById(Long id) {
        User user = userRepository.findWithProfileById(id).orElseThrow(
                () -> new ResourceNotFoundException("user", id)
        );
        return userMapper.toWithProfileResponse(user);
    }

    @Override
    @Transactional
    public UserResponse update(Long id, UpdateUserRequest request) {
        User user = userRepository.findByIdAndIsDeletedFalse(id).orElseThrow();
        if (userRepository.existsByPhoneAndIdNot(request.getPhone(), id)) {
            throw new DuplicateResourceException(
                    "Phone number already in use: " + request.getPhone());
        }

        userMapper.updateEntityFromRequest(request, user);
        return userMapper   .toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void softDelete(Long id) {
        User user = userRepository.findByIdAndIsDeletedFalse(id).orElseThrow();

        if (user.getRole() == UserRole.SUPER_ADMIN) {
            throw new BusinessRuleException(
                    "Super admin account cannot be deleted");
        }

        userRepository.softDelete(id, Instant.now());

        log.info("User soft-deleted: id={}", id);
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(Long id, UpdateUserProfileRequest request) {
        var user = userRepository.findByIdAndIsDeletedFalse(id).orElseThrow(
                () -> new ResourceNotFoundException("user", id)
        );

        if (request.getLicenseNumber() != null &&
                profileRepository.existsByLicenseNumberAndUserIdNot(
                        request.getLicenseNumber(), id)) {
            throw new DuplicateResourceException(
                    "License number already registered: " + request.getLicenseNumber());
        }

        UserProfile profile = profileRepository.findByUserId(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Profile not found for user id: " + id));

        userMapper.updateProfileFromRequest(request, profile);
        return userMapper.toProfileResponse(profileRepository.save(profile));
    }

    @Override
    @Transactional
    public void suspend(Long id, SuspendUserRequest request) {
        User user = userRepository.findByIdAndIsDeletedFalse(id).orElseThrow(
                () -> new ResourceNotFoundException("user", id)
        );

        if (user.getRole() == UserRole.SUPER_ADMIN) {
            throw new BusinessRuleException("Super admin cannot be suspended");
        }
        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new BusinessRuleException("User is already suspended");
        }

        userRepository.suspend(id, request.getReason(), Instant.now());
        log.info("User suspended: id={}, reason={}", id, request.getReason());
    }

    @Override
    @Transactional
    public void unsuspend(Long id) {
        User user = findActiveUserById(id);

        if (Boolean.TRUE.equals(user.getIsActive())) {
            throw new BusinessRuleException("User is not currently suspended");
        }

        userRepository.unsuspend(id);
        log.info("User unsuspended: id={}", id);

    }

    @Override
    public void changePassword(Long id, ChangePasswordRequest request) {
        User user = findActiveUserById(id);

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(),
                user.getPasswordHash())) {
            throw new BusinessRuleException("Current password is incorrect");
        }

        // Verify new passwords match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessRuleException(
                    "New password and confirm password do not match");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Password changed for user id={}", id);

    }

    @Override
    public PagedResponse<UserResponse> getAllUsers(Pageable pageable) {
        Page<User> page = userRepository.findByIsDeletedFalse(pageable);
        return toPagedResponse(page);
    }

    @Override
    public PagedResponse<UserResponse> getUsersByRole(UserRole role,
                                                      Pageable pageable) {
        Page<User> page = userRepository.findByRoleAndIsDeletedFalse(
                role, pageable);
        return toPagedResponse(page);
    }

    @Override
    public PagedResponse<UserSummaryResponse> getDrivers() {
        List<UserSummaryResponse> page = userRepository
                .findByRoleAndIsActiveTrueAndIsDeletedFalse(UserRole.DRIVER)
                .stream()
                .map(userMapper::toSummaryResponse)
                .toList();
        return PagedResponse.<UserSummaryResponse>builder()
                .content(page)
                .build();
    }

    private User findActiveUserById(Long id) {
        return userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    private PagedResponse<UserResponse> toPagedResponse(Page<User> page) {
        return PagedResponse.<UserResponse>builder()
                .content(page.getContent().stream()
                        .map(userMapper::toResponse)
                        .toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}
