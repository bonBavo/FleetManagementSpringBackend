package com.vibran.domain.user.controller;

import com.vibran.domain.user.dto.request.*;
import com.vibran.domain.user.dto.response.UserProfileResponse;
import com.vibran.domain.user.dto.response.UserResponse;
import com.vibran.domain.user.dto.response.UserSummaryResponse;
import com.vibran.domain.user.dto.response.UserWithProfileResponse;
import com.vibran.domain.user.enums.UserRole;
import com.vibran.domain.user.service.impl.UserServiceImpl;
import com.vibran.domain.user.service.interfaces.UserService;
import com.vibran.shared.response.ApiResponse;
import com.vibran.shared.response.PagedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    //user_service = new UserService();
    //userService = new UserService();

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("User created successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserWithProfileResponse>> getUser(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(userService.getById(id)));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable Long id) {

        userService.softDelete(id);
        return ResponseEntity.ok(
                ApiResponse.success("User deleted successfully", null));
    }

    @PutMapping("/{id}/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserProfileRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success("Profile updated",
                        userService.updateProfile(id, request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success("User updated", userService.update(id, request)));
    }

    @PutMapping("/{id}/suspend")
    public ResponseEntity<ApiResponse<Void>> suspend(
            @PathVariable Long id,
            @Valid @RequestBody SuspendUserRequest request) {

        userService.suspend(id, request);
        return ResponseEntity.ok(
                ApiResponse.success("User suspended", null));
    }

    @PostMapping("/{id}/unsuspend")
    public ResponseEntity<ApiResponse<Void>> unsuspend(
            @PathVariable Long id) {

        userService.unsuspend(id);
        return ResponseEntity.ok(
                ApiResponse.success("User unsuspended", null));
    }
    @PostMapping("/{id}/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable Long id,
            @Valid @RequestBody ChangePasswordRequest request) {

        userService.changePassword(id, request);
        return ResponseEntity.ok(
                ApiResponse.success("Password changed successfully", null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(
                ApiResponse.success(userService.getAllUsers(pageable)));
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<ApiResponse<PagedResponse<UserResponse>>> getUsersByRole(
            @PathVariable UserRole role,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("fullName").ascending());

        return ResponseEntity.ok(
                ApiResponse.success(userService.getUsersByRole(role, pageable)));
    }

    @GetMapping("/drivers")
    public ResponseEntity<ApiResponse<PagedResponse<UserSummaryResponse>>> getDrivers() {
        return ResponseEntity.ok(
                ApiResponse.success(userService.getDrivers()));
    }
}
