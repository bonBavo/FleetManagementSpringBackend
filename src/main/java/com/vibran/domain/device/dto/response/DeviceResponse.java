package com.vibran.domain.device.dto.response;

import com.vibran.shared.enums.DeviceStatus;
import lombok.Data;
import java.time.Instant;

@Data
public class DeviceResponse {
    private Long   id;
    private Long   vehicleId;
    private String vehiclePlate;
    private String deviceSerial;
    private String simPhoneNumber;
    private String firmwareVersion;
    private DeviceStatus status;
    private Instant lastSeenAt;
    private Instant assignedAt;
}
