package com.vibran.domain.device.service.interfaces;

import com.vibran.domain.device.dto.request.AssignDeviceRequest;
import com.vibran.domain.device.dto.request.RegisterDeviceRequest;
import com.vibran.domain.device.dto.response.DeviceResponse;

import java.util.List;

public interface DeviceService {
    DeviceResponse register(RegisterDeviceRequest request);
    DeviceResponse assign(Long deviceId, AssignDeviceRequest request);
    DeviceResponse deactivate(Long deviceId);
    DeviceResponse getById(Long id);
    List<DeviceResponse> getOfflineDevices(int thresholdMinutes);
}
