package com.vibran.domain.trip.dto.request;

import com.vibran.shared.enums.TripType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class StartTripRequest {
    @NotNull private Long vehicleId;
    @NotNull private Long deviceId;
    private Long driverId;
    private BigDecimal startLatitude;
    private BigDecimal startLongitude;
    private String startAddress;
    private TripType tripType;
    private String routeCode;          // matatu only
}
