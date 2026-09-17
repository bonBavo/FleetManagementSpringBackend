package com.vibran.domain.vehicle.dto.request;

import com.vibran.shared.enums.VehicleStatus;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateVehicleRequest {
    @Size(max = 50) private String color;
    @Size(max = 50) private String vin;
    private VehicleStatus status;
    private Integer seatingCapacity;
}
