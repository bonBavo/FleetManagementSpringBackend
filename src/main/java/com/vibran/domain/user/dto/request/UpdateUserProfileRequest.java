package com.vibran.domain.user.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UpdateUserProfileRequest {
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
}
