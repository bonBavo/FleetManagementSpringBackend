package com.vibran.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 150)
    private String fullName;

    @NotBlank(message = "Phone is required")
    @Size(min = 10, max = 20)
    @Pattern(regexp = "^\\+?[0-9]{10,20}$", message = "Phone number format is invalid")
    private String phone;
}
