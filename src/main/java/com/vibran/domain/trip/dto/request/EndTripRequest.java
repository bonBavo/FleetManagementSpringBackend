package com.vibran.domain.trip.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class EndTripRequest {
    private BigDecimal endLatitude;
    private BigDecimal endLongitude;
    private String     endAddress;
    private Integer    passengerCount;      // matatu only
    private BigDecimal fareCollectedKes;    // matatu only
}
