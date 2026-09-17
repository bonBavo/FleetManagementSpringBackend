package com.vibran.domain.device.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterDeviceRequest {
    @NotBlank @Size(max = 100) private String deviceSerial;
    @NotBlank @Size(max = 255) private String deviceToken;
    @Size(max = 30)  private String simIccid;
    @Size(max = 20)  private String simPhoneNumber;
    @Size(max = 50)  private String firmwareVersion;
}
