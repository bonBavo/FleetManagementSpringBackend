package com.vibran.domain.trip.dto.response;

import com.vibran.shared.enums.TripStatus;
import com.vibran.shared.enums.TripType;
import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;

@Data
public class TripResponse {
    private Long   id;
    private Long   vehicleId;
    private String vehiclePlate;
    private String driverName;
    private Instant startTime;
    private Instant endTime;
    private String startAddress;
    private String endAddress;
    private BigDecimal distanceKm;
    private Integer    durationMinutes;
    private BigDecimal maxSpeedKmh;
    private BigDecimal avgSpeedKmh;
    private BigDecimal fuelConsumedL;
    private TripStatus status;
    private TripType   tripType;
    private String     routeCode;
    private Integer    passengerCount;
    private BigDecimal fareCollectedKes;
}
