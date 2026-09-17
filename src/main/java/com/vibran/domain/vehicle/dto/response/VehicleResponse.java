package com.vibran.domain.vehicle.dto.response;

import com.vibran.shared.enums.*;
import lombok.Data;
import java.time.Instant;

@Data
public class VehicleResponse {
    private Long   id;
    private String plateNumber;
    private String makeName;       // denormalized for Flutter display
    private String modelName;
    private Integer year;
    private String color;
    private String vin;
    private PowertrainType  powertrainType;
    private VehicleCategory vehicleCategory;
    private Integer seatingCapacity;
    private VehicleStatus   status;
    private String          ownerName;
    private Instant createdAt;
}
