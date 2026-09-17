package com.vibran.domain.geofence.service;

import com.vibran.domain.geofence.dto.request.CreateGeofenceRequest;
import com.vibran.domain.geofence.dto.request.UpdateGeofenceRequest;
import com.vibran.domain.geofence.dto.response.GeofenceResponse;

import java.util.List;

public interface GeofenceService {
    GeofenceResponse       create(CreateGeofenceRequest request);
    GeofenceResponse       getById(Long id);
    GeofenceResponse       update(Long id, UpdateGeofenceRequest request);
    void                   deactivate(Long id);
    void                   delete(Long id);
    List<GeofenceResponse> getByOwner(Long ownerId);
    List<GeofenceResponse> getByVehicle(Long vehicleId);
}
