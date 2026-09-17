package com.vibran.domain.vehicle.dto.request;

import com.vibran.shared.enums.PowertrainType;
import com.vibran.shared.enums.VehicleCategory;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterVehicleRequest {

    @NotNull  private Long   ownerId;
    @NotNull  private Long   modelId;

    @NotBlank @Size(max = 20)
    private String plateNumber;

    @NotNull @Min(1980) @Max(2030)
    private Integer year;

    @Size(max = 50)  private String color;
    @Size(max = 50)  private String vin;

    @NotNull private PowertrainType   powertrainType;
    @NotNull private VehicleCategory  vehicleCategory;

    private Integer seatingCapacity;
}
