package com.vibran.domain.trip.service.interfaces;

import com.vibran.domain.trip.dto.request.*;
import com.vibran.domain.trip.dto.response.TripResponse;
import com.vibran.shared.response.PagedResponse;
import org.springframework.data.domain.Pageable;

public interface TripService {
    TripResponse  start(StartTripRequest request);
    TripResponse  end(Long tripId, EndTripRequest request);
    TripResponse  getById(Long id);
    TripResponse  getActiveTrip(Long vehicleId);
    PagedResponse<TripResponse> getVehicleHistory(Long vehicleId, Pageable p);
    PagedResponse<TripResponse> getDriverHistory(Long driverId, Pageable p);
}