package com.vibran.domain.sacco.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateSaccoRequest {
    @NotBlank @Size(max = 255) private String name;
    @NotBlank @Size(max = 100) private String registrationNumber;
    @Size(max = 500) private String routeDescription;
    @Size(max = 255) private String contactEmail;
    @Size(max = 20)  private String contactPhone;
    @Size(max = 100) private String county;
}
