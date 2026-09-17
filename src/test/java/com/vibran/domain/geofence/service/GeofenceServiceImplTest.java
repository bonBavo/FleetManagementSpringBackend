package com.vibran.domain.geofence.service;

import com.vibran.domain.geofence.dto.request.CreateGeofenceRequest;
import com.vibran.domain.geofence.dto.response.GeofenceResponse;
import com.vibran.domain.geofence.entity.Geofence;
import com.vibran.domain.geofence.mapper.GeofenceMapper;
import com.vibran.domain.geofence.repository.GeofenceRepository;
import com.vibran.domain.user.entity.User;
import com.vibran.domain.user.repositiory.UserRepository;
import com.vibran.domain.vehicle.entity.Vehicle;
import com.vibran.domain.vehicle.repository.VehicleRepository;
import com.vibran.shared.exception.BusinessRuleException;
import com.vibran.shared.exception.DuplicateResourceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeofenceServiceImplTest {

    @Mock private GeofenceRepository geofenceRepository;
    @Mock private UserRepository userRepository;
    @Mock private VehicleRepository vehicleRepository;
    @Mock private GeofenceMapper mapper;

    @InjectMocks private GeofenceServiceImpl geofenceService;

    @Test
    @DisplayName("Should successfully create a geofence")
    void shouldCreateGeofenceSuccessfully() {
        // Given
        CreateGeofenceRequest request = new CreateGeofenceRequest();
        request.setOwnerId(1L);
        request.setName("Warehouse");
        request.setVehicleId(10L);

        User owner = User.builder().id(1L).email("owner@test.com").build();
        Vehicle vehicle = Vehicle.builder().id(10L).owner(owner).build();
        Geofence geofence = new Geofence();
        Geofence saved = new Geofence();
        saved.setId(100L);

        when(userRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(owner));
        when(geofenceRepository.existsByOwnerIdAndNameAndIsActiveTrue(1L, "Warehouse")).thenReturn(false);
        when(vehicleRepository.findByIdAndIsDeletedFalse(10L)).thenReturn(Optional.of(vehicle));
        when(mapper.toEntity(request)).thenReturn(geofence);
        when(geofenceRepository.save(any(Geofence.class))).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(new GeofenceResponse());

        // When
        GeofenceResponse response = geofenceService.create(request);

        // Then
        assertNotNull(response);
        verify(geofenceRepository).save(any(Geofence.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when geofence name exists")
    void shouldThrowExceptionWhenNameExists() {
        // Given
        CreateGeofenceRequest request = new CreateGeofenceRequest();
        request.setOwnerId(1L);
        request.setName("Office");

        User owner = User.builder().id(1L).build();
        when(userRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(owner));
        when(geofenceRepository.existsByOwnerIdAndNameAndIsActiveTrue(1L, "Office")).thenReturn(true);

        // When & Then
        assertThrows(DuplicateResourceException.class, () -> geofenceService.create(request));
    }

    @Test
    @DisplayName("Should throw BusinessRuleException when vehicle does not belong to owner")
    void shouldThrowExceptionWhenVehicleMismatch() {
        // Given
        CreateGeofenceRequest request = new CreateGeofenceRequest();
        request.setOwnerId(1L);
        request.setVehicleId(10L);

        User owner = User.builder().id(1L).build();
        User stranger = User.builder().id(2L).build();
        Vehicle vehicle = Vehicle.builder().id(10L).owner(stranger).build();

        when(userRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(owner));
        when(vehicleRepository.findByIdAndIsDeletedFalse(10L)).thenReturn(Optional.of(vehicle));
        when(mapper.toEntity(request)).thenReturn(new Geofence()); // mapper.toEntity(request) happens before check

        // When & Then
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> geofenceService.create(request));
        assertEquals("Vehicle does not belong to this owner", ex.getMessage());
    }
}
