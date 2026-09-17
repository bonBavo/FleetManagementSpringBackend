package com.vibran.domain.user.dto.request;

import com.vibran.domain.user.enums.UserRole;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateUserRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 150, message = "Full name must be between 2 and 150 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    @Size(max = 255)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "Phone number is required")
    @Size(min = 10, max = 20, message = "Phone number must be between 10 and 20 characters")
    @Pattern(regexp = "^\\+?[0-9]{10,20}$", message = "Phone number format is invalid")
    private String phone;

    @NotNull(message = "Role is required")
    private UserRole role;


}
