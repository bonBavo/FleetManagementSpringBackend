package com.vibran.domain.user.dto.response;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Data
public class UserProfileResponse {
    private Long id;
    private String profilePhotoUrl;
    private LocalDate dateOfBirth;
    private String addressLine;
    private String city;
    private String country;
    private String licenseNumber;
    private LocalDate licenseExpiry;
    private String emergencyContact;
    private String emergencyPhone;
    private String bio;
    private Instant updatedAt;
}
