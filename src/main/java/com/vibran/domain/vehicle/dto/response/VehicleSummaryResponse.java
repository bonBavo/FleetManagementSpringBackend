package com.vibran.domain.vehicle.dto.response;

import com.vibran.shared.enums.*;
import lombok.Data;

@Data
public class VehicleSummaryResponse {
    private Long   id;
    private String plateNumber;
    private String makeName;
    private String modelName;
    private Integer year;
    private PowertrainType  powertrainType;
    private VehicleCategory vehicleCategory;
    private VehicleStatus   status;
}
